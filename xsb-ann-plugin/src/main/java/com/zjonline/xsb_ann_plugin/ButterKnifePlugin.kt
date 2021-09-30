package com.zjonline.xsb_ann_plugin


import com.android.build.gradle.*
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.res.GenerateLibraryRFileTask
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import groovy.util.XmlSlurper
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

/**
 * Created by lizhi
 * Date: 2021/8/9
 */
class ButterKnifePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.all {
            when (it) {
                is FeaturePlugin -> {
                    project.extensions[FeatureExtension::class].run {
//                            configureR2Generation(project, featureVariants)
//                            configureR2Generation(project, libraryVariants)
                    }
                }
                is LibraryPlugin -> {
                    project.extensions[LibraryExtension::class].run {
                        configureR2Generation(project, libraryVariants)
                    }
                }
                is AppPlugin -> {
                    project.extensions[AppExtension::class].run {
                        configureR2Generation(project, applicationVariants)
                    }
                }
            }
        }
    }

    private fun getPackageName(variant: BaseVariant): String {
        val slurper = XmlSlurper(false, false)
        val list = variant.sourceSets.map { it.manifestFile }

        val result = slurper.parse(list[0])
        return result.getProperty("@package").toString()
    }

    private fun configureR2Generation(project: Project, variants: DomainObjectSet<out BaseVariant>) {
        variants.all { variant ->
            val outputDir = project.buildDir.resolve(
                    "generated/source/r2/${variant.dirName}")

            val rPackage = getPackageName(variant)
            val once = AtomicBoolean()
            variant.outputs.all { output ->
                if (once.compareAndSet(false, true)) {
                    val processResources = output.processResourcesProvider.get() // TODO lazy
                    if (processResources.sourceOutputDir.absolutePath.endsWith(".jar")){
                        val rFile =
                                project.files(
                                        when (processResources) {
                                            is GenerateLibraryRFileTask -> processResources.getTextSymbolOutputFile()
                                            is LinkApplicationAndroidResourcesTask -> processResources.getTextSymbolOutputFile()
                                            else -> throw RuntimeException("最低支持build:gradle:3.3.0请去build.gradle中修改com.android.tools.build:gradle")
                                        })
                                        .builtBy(processResources)
                        val generate = project.tasks.create("generate${variant.name.capitalize()}R2", R2Generator::class.java) {
                            it.outputDir = outputDir
                            it.rFile = rFile
                            it.packageName = rPackage
                            it.className = "R2"
                        }
                        variant.registerJavaGeneratingTask(generate, outputDir)
                    }else{
                        val task = project.tasks.create("generate${variant.name.capitalize()}R2")
                        task.outputs.dir(outputDir)
                        variant.registerJavaGeneratingTask(task, outputDir)
                        task.dependsOn(processResources)
                        val pathToR = rPackage.replace('.', File.separatorChar)
                        val rFile = processResources.sourceOutputDir.resolve(pathToR).resolve("R.java")
                        task.apply {
                            inputs.file(rFile)

                            doLast {
                                CreateR.brewJava(rFile, outputDir, rPackage, "R2")
                            }
                        }
                    }
                }
            }
        }
    }

    private operator fun <T : Any> ExtensionContainer.get(type: KClass<T>): T {
        return getByType(type.java)
    }
}