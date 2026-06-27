package com.vidyasetuai.feature_case_study.domain.model

import kotlinx.serialization.json.*

sealed class ContentBlock {
    data class Title(val text: String) : ContentBlock()
    data class Section(val text: String) : ContentBlock()
    data class SubSection(val text: String) : ContentBlock()
    data class Paragraph(val text: String) : ContentBlock()
    data class ImageBlock(val url: String, val caption: String?) : ContentBlock()
    data class VideoLink(val url: String, val title: String?, val thumbnailUrl: String?) : ContentBlock()
    data class Quote(val text: String, val author: String?) : ContentBlock()
    data class Checklist(val items: List<String>) : ContentBlock()
    data class ResourceLink(val url: String, val label: String) : ContentBlock()
    data class Tip(val text: String) : ContentBlock()
    data class Warning(val text: String) : ContentBlock()
    data class Unknown(val type: String) : ContentBlock()

    companion object {
        fun fromJson(jsonObject: JsonObject): ContentBlock {
            val type = jsonObject["type"]?.jsonPrimitive?.content ?: "unknown"
            return when (type) {
                "title" -> Title(
                    text = jsonObject["text"]?.jsonPrimitive?.content ?: ""
                )
                "section" -> Section(
                    text = jsonObject["text"]?.jsonPrimitive?.content ?: ""
                )
                "sub_section" -> SubSection(
                    text = jsonObject["text"]?.jsonPrimitive?.content ?: ""
                )
                "paragraph" -> Paragraph(
                    text = jsonObject["text"]?.jsonPrimitive?.content ?: ""
                )
                "image" -> ImageBlock(
                    url = jsonObject["url"]?.jsonPrimitive?.content ?: "",
                    caption = jsonObject["caption"]?.jsonPrimitive?.contentOrNull
                )
                "video_link" -> VideoLink(
                    url = jsonObject["url"]?.jsonPrimitive?.content ?: "",
                    title = jsonObject["title"]?.jsonPrimitive?.contentOrNull,
                    thumbnailUrl = jsonObject["thumbnail_url"]?.jsonPrimitive?.contentOrNull
                )
                "quote" -> Quote(
                    text = jsonObject["text"]?.jsonPrimitive?.content ?: "",
                    author = jsonObject["author"]?.jsonPrimitive?.contentOrNull
                )
                "checklist" -> Checklist(
                    items = jsonObject["items"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
                )
                "resource_link" -> ResourceLink(
                    url = jsonObject["url"]?.jsonPrimitive?.content ?: "",
                    label = jsonObject["label"]?.jsonPrimitive?.content ?: "Link"
                )
                "tip" -> Tip(
                    text = jsonObject["text"]?.jsonPrimitive?.content ?: ""
                )
                "warning" -> Warning(
                    text = jsonObject["text"]?.jsonPrimitive?.content ?: ""
                )
                else -> Unknown(type)
            }
        }

        fun ContentBlock.toJsonObject(): JsonObject {
            return buildJsonObject {
                when (this@toJsonObject) {
                    is Title -> {
                        put("type", "title")
                        put("text", text)
                    }
                    is Section -> {
                        put("type", "section")
                        put("text", text)
                    }
                    is SubSection -> {
                        put("type", "sub_section")
                        put("text", text)
                    }
                    is Paragraph -> {
                        put("type", "paragraph")
                        put("text", text)
                    }
                    is ImageBlock -> {
                        put("type", "image")
                        put("url", url)
                        put("caption", caption)
                    }
                    is VideoLink -> {
                        put("type", "video_link")
                        put("url", url)
                        put("title", title)
                        put("thumbnail_url", thumbnailUrl)
                    }
                    is Quote -> {
                        put("type", "quote")
                        put("text", text)
                        put("author", author)
                    }
                    is Checklist -> {
                        put("type", "checklist")
                        putJsonArray("items") {
                            items.forEach { add(it) }
                        }
                    }
                    is ResourceLink -> {
                        put("type", "resource_link")
                        put("url", url)
                        put("label", label)
                    }
                    is Tip -> {
                        put("type", "tip")
                        put("text", text)
                    }
                    is Warning -> {
                        put("type", "warning")
                        put("text", text)
                    }
                    is Unknown -> {
                        put("type", type)
                    }
                }
            }
        }
    }
}
