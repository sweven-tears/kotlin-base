package com.app.test.utils
import com.google.gson.Gson
import pers.sweven.common.utils.JsonTool as Json

class JsonTool : Json(){
    override val gson: Gson get() = Gson()
}