package io.github.notoday.jetbrains.paste.file.into.markdown.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 *
 * @author no-today
 * @date 2023/05/14 22:29
 */
class AppSettingsConfigurable : Configurable {

    private lateinit var appSettingsComponent: AppSettingsComponent

    override fun createComponent(): JComponent {
        appSettingsComponent = AppSettingsComponent(AppSettingsState.instance.state.deepCopy())
        return appSettingsComponent.panel
    }

    override fun reset() {
        appSettingsComponent.state = AppSettingsState.instance.state.deepCopy()
        appSettingsComponent.panel.reset()
    }

    override fun isModified(): Boolean {
        // https://plugins.jetbrains.com/docs/intellij/kotlin-ui-dsl-version-2.html#cellbind
        appSettingsComponent.panel.apply()
        return AppSettingsState.instance.state != appSettingsComponent.state
    }

    override fun apply() {
        AppSettingsState.instance.state = appSettingsComponent.state.deepCopy()
    }

    override fun getDisplayName(): String {
        return ""
    }
}