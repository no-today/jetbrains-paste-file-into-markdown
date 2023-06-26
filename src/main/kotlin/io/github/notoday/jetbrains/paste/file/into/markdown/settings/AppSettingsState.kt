package io.github.notoday.jetbrains.paste.file.into.markdown.settings

import com.intellij.configurationStore.APP_CONFIG
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * see https://plugins.jetbrains.com/docs/intellij/settings-tutorial.html
 *
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 *
 * @author no-today
 * @date 2023/05/14 22:15
 */
@State(
    name = "io.github.notoday.jetbrains.paste.file.into.markdown.config.IPluginConfig",
    storages = [Storage("$APP_CONFIG/paste-file-into-markdown.xml")]
)
class AppSettingsState : PersistentStateComponent<IState> {

    private var state: IState = IState()

    override fun getState(): IState {
        return this.state
    }

    fun setState(state: IState) {
        this.state = state
    }

    override fun loadState(state: IState) {
        XmlSerializerUtil.copyBean(state, this.state)
    }

    companion object {
        val instance: AppSettingsState
            get() = ServiceManager.getService(AppSettingsState::class.java)
    }
}