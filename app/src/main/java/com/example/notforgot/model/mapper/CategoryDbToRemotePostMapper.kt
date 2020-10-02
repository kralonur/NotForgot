package com.example.notforgot.model.mapper

import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.remote.items.category.CategoryPost

object CategoryDbToRemotePostMapper : Mapper<DbCategory, CategoryPost> {
    override fun map(input: DbCategory): CategoryPost {
        return CategoryPost(input.name)
    }
}