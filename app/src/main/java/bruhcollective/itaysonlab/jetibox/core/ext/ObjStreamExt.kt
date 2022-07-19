package bruhcollective.itaysonlab.jetibox.core.ext

import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderDataItem
import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderLayer
import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderLayoutItem

fun List<ContentBuilderLayoutItem>.extractTitlesFromCBLayout(): List<Long> = mapNotNull { item -> item.channelData }
    .flatMap { data -> data.items }
    .extractTitlesFromCBItem()

fun List<ContentBuilderDataItem>.extractTitlesFromCBItem(): List<Long> = flatMap { items -> items.itemLayers }
    .filterIsInstance<ContentBuilderLayer.GameTitle>()
    .flatMap { titles -> titles.data.titles }