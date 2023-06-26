# Paste file into markdown

## RoadMap

- [x] Local
- [x] Qiniu

## References

- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [IntelliJ Platform UI Guidelines](https://jetbrains.design/intellij/)
- [jetbrains-markdown-image-support](https://github.com/wenzewoo/jetbrains-markdown-image-support)

## Notice

- [DialogPanel.apply() must be executed before the data of the view bind will be synchronized](https://plugins.jetbrains.com/docs/intellij/kotlin-ui-dsl-version-2.html#cellbind)

Binds component value that is provided by componentGet and componentSet methods to specified binding property. The
property is applied only when **DialogPanel.apply()** is invoked. Methods DialogPanel.isModified() and
DialogPanel.reset() are also supported automatically for bound properties.