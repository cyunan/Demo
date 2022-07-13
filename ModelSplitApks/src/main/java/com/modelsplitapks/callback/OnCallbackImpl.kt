package com.modelsplitapks.callback

/**
 *    author : ChenYuNan
 *    date   : 2022/7/13
 *    desc   :
 */
class OnCallbackImpl(
    private var preInstall: (() -> Unit)? = null,
    private var inInstall: (() -> Unit)? = null,
    private var successInstall: (() -> Unit)? = null,
    private var errorInstall: ((String) -> Unit)? = null,
    ): OnCallback{

    fun preInstall(listener: ()->Unit){
        this.preInstall = listener
    }
    fun inInstall(listener: ()->Unit){
        this.inInstall = listener
    }
    fun successInstall(listener: ()->Unit){
        this.successInstall = listener
    }
    fun errorInstall(listener: (String)->Unit){
        this.errorInstall = listener
    }

    override fun preInstall() {
        this.preInstall?.invoke()
    }

    override fun inInstall() {
        this.inInstall?.invoke()
    }

    override fun successInstall() {
        this.successInstall?.invoke()
    }

    override fun errorInstall(error: String) {
        this.errorInstall?.invoke(error)
    }

}