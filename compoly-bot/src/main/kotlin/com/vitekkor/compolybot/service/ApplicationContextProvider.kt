package com.vitekkor.compolybot.service

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service

@Service
class ApplicationContextProvider: ApplicationContextAware {

    companion object {
        lateinit var applicationContext: ApplicationContext
            private set
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        Companion.applicationContext = applicationContext
    }
}

inline fun <reified T> bean() = lazy { ApplicationContextProvider.applicationContext.getBean(T::class.java) }