package io.github.notoday.jetbrains.paste.file.into.markdown.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.text.Strings
import com.intellij.ui.dsl.builder.*
import io.github.notoday.jetbrains.paste.file.into.markdown.Flag
import io.github.notoday.jetbrains.paste.file.into.markdown.helper.QiniuStorageHelper
import javax.swing.JPanel


/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 *
 * @author no-today
 * @date 2023/05/14 22:29
 */
class AppSettingsComponent(var state: IState) {

    companion object {
        const val formWidthGroup = "widthGroup"

        const val defaultLength = 35
        const val protocolLength = 7
        const val domainLength = 20

        const val labelSavePath = "Save path:"
        const val labelNewFilename = "New filename:"
        const val labelImageQuality = "Image quality:"

        const val labelAccessKey = "Access key:"
        const val labelSecretKey = "Secret key:"
        const val labelBucketName = "Bucket name:"
        const val labelBucketDomain = "Bucket domain:"

        const val buttonTest = "Test"

        const val messageTitle = "Test Result"
        const val messageSuccess = "Config is OK"
    }

    private lateinit var domainList: ComboBox<String>

    val panel = panel {
        buttonsGroup {
            row {
                for (value in StoragePolicy.values()) {
                    radioButton(value.v, value)
                }
            }
        }.bind<StoragePolicy>(getter = { state.storagePolicy }, setter = { state.storagePolicy = it })

        group("Basic") {
            row(labelSavePath) {
                comboBox(Basic.optionsSavePath)
                    .widthGroup(formWidthGroup)
                    .columns(defaultLength)
                    .bindItem(getter = { state.basic.savePath }, setter = { state.basic.savePath = it.orEmpty() })
                    .component.isEditable = true
            }

            row(labelNewFilename) {
                comboBox(Basic.optionsNewFilename)
                    .widthGroup(formWidthGroup)
                    .columns(defaultLength)
                    .bindItem(getter = { state.basic.newFilename }, setter = { state.basic.newFilename = it.orEmpty() })
                    .component.isEditable = true
            }

            row(labelImageQuality) {
                slider(50, 100, 5, 25)
                    .widthGroup(formWidthGroup)
                    .bindValue(state.basic::imageQuality)
            }
        }

        group("Qiniu") {
            row(labelAccessKey) {
                textField()
                    .widthGroup(formWidthGroup)
                    .columns(defaultLength)
                    .bindText(getter = { state.qiniu.accessKey }, setter = { state.qiniu.accessKey = it })
            }

            row(labelSecretKey) {
                textField()
                    .widthGroup(formWidthGroup)
                    .columns(defaultLength)
                    .bindText(getter = { state.qiniu.secretKey }, setter = { state.qiniu.secretKey = it })
            }

            row(labelBucketName) {
                textField()
                    .widthGroup(formWidthGroup)
                    .columns(defaultLength)
                    .bindText(getter = { state.qiniu.bucketName }, setter = { state.qiniu.bucketName = it })
            }

            row(labelBucketDomain) {
                comboBox(Qiniu.optionsProtocol)
                    .columns(protocolLength)

                domainList = comboBox(state.qiniu.domainList)
                    .bindItem(
                        getter = { state.qiniu.bucketDomain },
                        setter = { state.qiniu.bucketDomain = it.orEmpty() })
                    .columns(domainLength)
                    .component
            }

            row {
                button(buttonTest, actionListener = {
                    try {
                        panel().apply()

                        state.qiniu.domainList.clear()
                        domainList.removeAllItems()

                        val bucketDomains = QiniuStorageHelper.refreshToken(state.qiniu, true)
                            .getBucketDomains(state.qiniu.bucketName)

                        state.qiniu.domainList.addAll(bucketDomains)
                        state.qiniu.bucketDomain = bucketDomains.first()

                        state.qiniu.domainList.forEach { domainList.addItem(it) }

                        Messages.showInfoMessage(messageSuccess, messageTitle)
                    } catch (e: Exception) {
                        Messages.showErrorDialog(e.message.orEmpty(), messageTitle)
                    } finally {
                        panel().apply()
                    }
                })
            }
        }

        row {
            val lis = Strings.join(Flag.values().map { "<li><b>${it.placeholder}:</b> ${it.desc}</li>" }, "</br>")
            val description = "<b>Variables:</b><ul>${lis}</ul>"
            text(description)
        }
    }

    fun panel(): DialogPanel = panel
}