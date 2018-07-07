// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package maksimaleshko.intellij.plugin.rterminal

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.util.SystemInfo
import java.io.File

/**
 * @author traff
 */
@State(name = "RTerminalApplicationOptionsProvider", storages = [(Storage("rterminal.xml"))])
class RTerminalApplicationOptionsProvider : PersistentStateComponent<RTerminalApplicationOptionsProvider.State> {
    class State {
        var myShellPath: String? = null
        var myTabName: String = "Local"
        var mySoundBell: Boolean = true
        var myCloseSessionOnLogout: Boolean = true
        var myReportMouse: Boolean = true
        var myCopyOnSelection: Boolean = true
        var myPasteOnMiddleMouseButton: Boolean = true
        var myOverrideIdeShortcuts: Boolean = true
        var myShellIntegration: Boolean = true
        var myHighlightHyperlinks: Boolean = true
    }

    private val myState = State()

    override fun getState(): State? {
        return myState
    }

    override fun loadState(state: State) {
        myState.myShellPath = state.myShellPath
        myState.myTabName = state.myTabName
        myState.mySoundBell = state.mySoundBell
        myState.myCloseSessionOnLogout = state.myCloseSessionOnLogout
        myState.myReportMouse = state.myReportMouse
        myState.myCopyOnSelection = state.myCopyOnSelection
        myState.myPasteOnMiddleMouseButton = state.myPasteOnMiddleMouseButton
        myState.myOverrideIdeShortcuts = state.myOverrideIdeShortcuts
        myState.myShellIntegration = state.myShellIntegration
        myState.myHighlightHyperlinks = state.myHighlightHyperlinks
    }

    var shellPath: String? by ValueWithDefault(State::myShellPath, myState) { defaultShellPath }

    var tabName: String
        get() = myState.myTabName
        set(value) {
            myState.myTabName = value
        }

    var soundBell: Boolean
        get() = myState.mySoundBell
        set(value) {
            myState.mySoundBell = value
        }

    var closeSessionOnLogout: Boolean
        get() = myState.myCloseSessionOnLogout
        set(value) {
            myState.myCloseSessionOnLogout = value
        }

    var reportMouse: Boolean
        get() = myState.myReportMouse
        set(value) {
            myState.myReportMouse = value
        }

    var copyOnSelection: Boolean
        get() = myState.myCopyOnSelection
        set(value) {
            myState.myCopyOnSelection = value
        }

    var pasteOnMiddleMouseButton: Boolean
        get() = myState.myPasteOnMiddleMouseButton
        set(value) {
            myState.myPasteOnMiddleMouseButton = value
        }

    var overrideIdeShortcuts: Boolean
        get() = myState.myOverrideIdeShortcuts
        set(value) {
            myState.myOverrideIdeShortcuts = value
        }

    var shellIntegration: Boolean
        get() = myState.myShellIntegration
        set(value) {
            myState.myShellIntegration = value
        }

    var highlightHyperlinks: Boolean
        get() = myState.myHighlightHyperlinks
        set(value) {
            myState.myHighlightHyperlinks = value
        }

    val defaultShellPath: String
        get() {
            val shell = System.getenv("SHELL")

            if (shell != null && File(shell).canExecute()) {
                return shell
            }

            if (SystemInfo.isUnix) {
                if (File("/bin/bash").exists()) {
                    return "/bin/bash"
                } else {
                    return "/bin/sh"
                }
            } else {
                return "cmd.exe"
            }
        }

    companion object {
        fun getInstance(): RTerminalApplicationOptionsProvider {
            return ServiceManager.getService(RTerminalApplicationOptionsProvider::class.java)
        }
    }
}





