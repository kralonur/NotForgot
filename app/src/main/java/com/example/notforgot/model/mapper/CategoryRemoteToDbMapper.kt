package com.example.notforgot.model.mapper

import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.remote.items.category.Category

object CategoryRemoteToDbMapper : Mapper<Category, DbCategory> {
    override fun map(input: Category): DbCategory {
        return DbCategory(input.id, input.name)
    }
}