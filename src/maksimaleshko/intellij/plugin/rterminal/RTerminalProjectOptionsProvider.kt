// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package maksimaleshko.intellij.plugin.rterminal

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager

/**
 * @author traff
 */
@State(name = "RTerminalProjectOptionsProvider", storages = [(Storage("rterminal.xml"))])
class RTerminalProjectOptionsProvider(val project: Project) : PersistentStateComponent<RTerminalProjectOptionsProvider.State> {
    class State {
        var myStartingDirectory: String? = null
        var myShellPath: String? = null
        var myTabName: String = "Local"
    }

    private val myState = State()

    override fun getState(): State? {
        return myState
    }

    override fun loadState(state: State) {
        myState.myStartingDirectory = state.myStartingDirectory
        myState.myShellPath = state.myShellPath
        myState.myTabName = state.myTabName
    }

    var startingDirectory: String? by ValueWithDefault(State::myStartingDirectory, myState) { defaultStartingDirectory }

    var shellPath: String? by ValueWithDefault(State::myShellPath, myState) { defaultShellPath }

    var tabName: String
        get() = myState.myTabName
        set(tabName) {
            myState.myTabName = tabName
        }

    val defaultStartingDirectory: String?
        get() {
            var directory: String? = null
            for (customizer in LocalTerminalCustomizer.EP_NAME.extensions) {
                try {

                    if (directory == null) {
                        directory = customizer.getDefaultFolder(project)
                    }
                } catch (e: Exception) {
                    LOG.error("Exception during getting default folder", e)
                }
            }

            return directory ?: currentProjectFolder()
        }

    val defaultShellPath: String?
        get() {
            return RTerminalApplicationOptionsProvider.getInstance().shellPath
        }

    private fun currentProjectFolder(): String? {
        val projectRootManager = ProjectRootManager.getInstance(project)

        val roots = projectRootManager.contentRoots
        if (roots.size == 1) {
            roots[0].canonicalPath
        }
        val baseDir = project.baseDir
        return baseDir?.canonicalPath
    }

    companion object {
        private val LOG = Logger.getInstance(RTerminalProjectOptionsProvider::class.java)

        fun getInstance(project: Project): RTerminalProjectOptionsProvider {
            return ServiceManager.getService(project, RTerminalProjectOptionsProvider::class.java)
        }
    }
}



