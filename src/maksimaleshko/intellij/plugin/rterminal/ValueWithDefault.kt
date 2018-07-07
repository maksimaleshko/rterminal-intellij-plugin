package maksimaleshko.intellij.plugin.rterminal

import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

class ValueWithDefault<S>(val prop: KMutableProperty1<S, String?>, val state: S, val default: () -> String?) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        return if (prop.get(state) !== null) prop.get(state) else default()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        prop.set(state, if (value == default() || value.isNullOrEmpty()) null else value)
    }
}