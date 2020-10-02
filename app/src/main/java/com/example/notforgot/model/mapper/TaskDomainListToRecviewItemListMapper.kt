package com.example.notforgot.model.mapper

import com.example.notforgot.model.domain.RecviewItem
import com.example.notforgot.model.domain.TaskDomain

object TaskDomainListToRecviewItemListMapper : Mapper<List<TaskDomain>, List<RecviewItem>> {
    override fun map(input: List<TaskDomain>): List<RecviewItem> {
        val map = input.groupBy { item -> item.category }
        val list = ArrayList<RecviewItem>()
        map.keys.forEach { k ->
            list.add(RecviewItem(false, category = k))
            map[k]?.forEach { v ->
                list.add(RecviewItem(true, task = v))
            }
        }
        return list

    }
}