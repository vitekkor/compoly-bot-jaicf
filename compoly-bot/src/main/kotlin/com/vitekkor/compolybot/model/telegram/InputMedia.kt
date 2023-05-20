package com.vitekkor.compolybot.model.telegram

import com.github.kotlintelegrambot.entities.inputmedia.InputMediaFields
import com.github.kotlintelegrambot.entities.inputmedia.InputMediaTypes
import com.google.gson.annotations.SerializedName

sealed interface InputMedia

data class InputMediaPhoto(
    @SerializedName(InputMediaFields.MEDIA)
    val media: String,
    @SerializedName(InputMediaFields.CAPTION)
    val caption: String? = null,
    @SerializedName(InputMediaFields.PARSE_MODE)
    val parseMode: String? = null,
    @SerializedName(InputMediaFields.TYPE)
    val type: String = InputMediaTypes.PHOTO
) : InputMedia