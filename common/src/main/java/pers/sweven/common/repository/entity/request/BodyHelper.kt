package pers.sweven.common.repository.entity.request

import com.google.gson.*
import java.util.logging.Logger

/**
 * 整体架构与功能概述 BodyHelper 类现在的设计更加灵活且功能丰富，
 * 它不仅可以辅助构建 JsonObject，还提供了方便的参数设置相关方法，
 * 用于管理构建请求体时所需的数据（以 HashMap 形式存储参数）。
 * 核心功能依旧围绕着将存储的各种类型参数转换为 JsonObject，
 * 同时支持通过 lambda 表达式定制复杂类型转换逻辑，并且对异常处理进行了细化，整体旨在更稳健、灵活地满足不同业务场景下构建 JSON 格式请求体的需求。
 * 具体方法分析
 * 1. 构造函数与参数管理相关方法构造函数：定义了一个带有默认参数的构造函数，
 * 初始化了一个空的 HashMap<String, Any?> 用于存储请求体相关的参数数据，
 * 这种设计允许用户在创建 BodyHelper 实例时选择是否传入初始的参数映射，提高了使用的灵活性。
 * putAll 方法：功能是清除当前实例内部存储参数的 map，然后将传入的 map 参数中的所有键值对添加进来，最后返回当前 BodyHelper 实例本身。
 * 这符合链式调用的风格，方便连续操作设置多个参数，
 * 例如可以像 bodyHelper.putAll(map1).putAll(map2) 这样连续调用添加不同来源的参数集合，使得参数设置过程更加简洁明了。
 * put 方法：接收一个键 key 和值 value，将 value 存入内部的 map 中对应的 key 位置，同样返回 BodyHelper 实例自身，支持链式调用，方便逐个添加参数，
 * 比如 bodyHelper.put("key1", value1).put("key2", value2)。
 * putIfAbsent 方法：按照 HashMap 的 putIfAbsent 方法逻辑，
 * 只有当指定的 key 在内部 map 中不存在时，才将传入的 value 存入，也返回 BodyHelper 实例，
 * 便于在有条件地添加参数场景下使用，有助于保证参数设置的准确性和避免重复设置。
 * 2. body 方法功能与逻辑：作为核心方法用于生成最终的 JsonObject。
 * 它接收一个可选的 lambda 表达式参数 converter，类型为 ((BodyHelper, Any?) -> JsonElement)?，
 * 这里相比之前版本，lambda 表达式多了一个 BodyHelper 类型的参数，
 * 可用于在转换逻辑中获取当前 BodyHelper 实例的一些信息（虽然目前代码中未体现具体使用场景，但增加了扩展性）。
 * 在方法内部，首先尝试获取传入 lambda 表达式转换后的 JsonObjectConverter 接口实现对象 func，
 * 如果 converter 为 null，则通过 elementConverter 方法结合默认的转换逻辑创建 func。
 * 然后创建一个空的 JsonObject 对象 obj，遍历内部存储参数的 map，
 * 针对每个键值对，在 try-catch 块中调用 obj.addElement 方法将数据添加到 obj 中，
 * 捕获并记录可能出现的 IllegalArgumentException 异常及相关详细信息（键名、值、异常消息等），方便排查问题，最后返回构建好的 JsonObject。
 * 优点与特点：
 * 通过新的 lambda 表达式参数设计以及和其他方法的协作，在维持可定制复杂类型转换逻辑功能的基础上，
 * 进一步增强了灵活性，同时异常处理机制保证了在出现数据类型不匹配等问题时能有相应的记录和应对策略，有助于提高构建请求体过程的稳定性。
 * 3. JsonObject 的 addElement 方法功能与逻辑：
 * 该方法用于将不同类型的元素添加到 JsonObject 中，在添加之前先对元素是否为 null 进行了判断，
 * 如果元素为 null，直接将其添加到 JsonObject（依赖于 JsonObject 对 null 值的处理机制，在 JSON 格式中 null 是合法的值表示形式）。
 * 对于非 null 元素，通过 when 表达式进行类型判断处理。处理列表类型元素时，遍历列表中的非 null 元素，
 * 调用 convertPrimitiveElement 方法将元素转换为 JsonElement 后添加到新创建的 JsonArray 中，
 * 期间捕获可能出现的类型转换异常并记录详细信息；对于基本数据类型（String、Number、Boolean、Char），
 * 直接使用对应的 addProperty 方法添加到 JsonObject 中；对于其他复杂类型，
 * 调用传入的 converter 的 convert 方法进行转换并添加，同样捕获和记录转换过程中出现的异常情况。
 * 优点与特点：
 * 细致地处理了各种类型元素添加到 JsonObject 的情况，尤其是对 null 值以及可能出现的异常情况考虑周全，
 * 通过合理的类型判断和复用相关转换方法，清晰地构建了 JsonObject 的结构，遵循了 JSON 格式要求并保证了整个添加元素过程的健壮性。
 * 4. convertPrimitiveElement 方法
 * 功能与逻辑：
 * 此方法用于将基本元素转换为 JsonElement，首先对传入的元素值是否为 null 进行判断，
 * 如果为 null，直接返回 JsonNull.INSTANCE（假设 JsonNull 是符合 JSON 规范的表示 null 值的对象，这里体现了对 null 值在 JSON 转换中的正确处理）。
 * 对于非 null 值，优先使用传入的自定义转换逻辑（通过 converter 参数）进行转换，
 * 如果 converter 为 null，则通过 when 表达式按照默认的类型判断规则进行转换，
 * 针对不同的基本类型（String、Number、Boolean、Char）以及实现了 HasJsonObject 接口的类型、List<*> 类型分别进行相应的 JsonElement 创建或获取操作，
 * 对于不符合这些预期类型的元素抛出 IllegalArgumentException 异常，并记录详细的日志信息，确保转换过程遵循既定的类型规范且在出现问题时能及时反馈。
 * 优点与特点：
 * 既提供了灵活接入外部自定义转换逻辑的能力，又定义了清晰完整的默认转换规则，
 * 对 null 值的处理完善了 JSON 数据转换的全面性，
 * 异常处理和日志记录增强了方法的健壮性和可调试性，整体使得元素到 JsonElement 的转换过程更加可靠、通用。
 * 5. JsonArray 的 addPrimitiveElement 方法
 * 功能与逻辑：
 * 该方法用于将元素添加到 JsonArray 中，同样先判断元素是否为 null，如果是则添加 JsonNull.INSTANCE 到 JsonArray 中，
 * 保证了对 null 值的正确处理符合 JSON 格式要求。
 * 对于非 null 元素，通过 when 表达式依据元素类型进行不同的添加操作，针对基本数据类型和实现了 HasJsonObject 接口的类型，
 * 分别将对应的 JsonElement（如 JsonPrimitive 或通过 jsonObject 方法获取的）添加到 JsonArray 中，
 * 对于不符合要求的类型抛出 IllegalArgumentException 异常并记录严重级别的日志信息，以此维护 JsonArray 中元素类型的合法性和 JSON 结构的正确性。
 * 优点与特点：严格按照 JSON 格式规范处理不同类型元素添加到 JsonArray 的情况，异常处理机制有助于及时发现和定位因元素类型错误导致的问题，
 * 确保构建出的 JsonArray 是符合要求的，为正确构建整个 JsonObject 作为请求体奠定了基础。
 * 6. ((BodyHelper, Any?) -> JsonElement)?.func() 与 elementConverter 方法
 * ((BodyHelper, Any?) -> JsonElement)?.func() 方法：
 * 这是针对特定 lambda 表达式类型的扩展方法，作用是将传入的 lambda 表达式（如果不为 null）转换为 JsonObjectConverter 接口实现对象，
 * 在转换过程中创建的匿名对象的 convert 方法里，调用传入的 lambda 表达式并传入当前 BodyHelper 实例和对应的值参数进行转换操作，
 * 实现了从 lambda 表达式到接口实现的适配转换，方便在需要 JsonObjectConverter 类型的地方使用，
 * 体现了 Kotlin 语言利用扩展方法处理函数类型与接口类型衔接的灵活性和便利性。
 * elementConverter 方法：
 * 接收一个 (BodyHelper, Any?) -> JsonElement 类型的函数参数 value，
 * 调用其 func 扩展方法并确保返回的结果不为 null（通过 !! 操作符），将其转换为 JsonObjectConverter 类型并返回。
 * 本质上是对 func 扩展方法的一种包装使用，简化了在 body 等方法中获取 JsonObjectConverter 接口对象的操作，使得代码逻辑更加清晰简洁，便于理解和维护。
 * 总体评价与进一步优化建议
 * 1. 总体评价
 * 整体优化后的代码在功能完整性、灵活性以及健壮性方面都有不错的表现。
 * 通过提供参数管理相关方法，方便了请求体参数的设置；
 * 核心的 JsonObject 构建逻辑借助 lambda 表达式实现了复杂类型转换的可定制化，
 * 并且各个方法在处理不同类型数据、应对 null 值以及异常情况等方面都考虑得较为细致，
 * 通过详细的日志记录有助于在出现问题时快速定位和解决，符合构建 JSON 格式请求体这一功能需求以及应对实际业务场景中各种复杂情况的要求。
 *
 *
 * Created by Sweven on 2024/12/20--11:47.
 * Email: sweventears@163.com
 */
class BodyHelper(private val map: HashMap<String, Any?> = hashMapOf()) {

    // 用于记录日志的Logger实例，方便在异常等情况下记录详细信息
    private val logger = Logger.getLogger(BodyHelper::class.java.name)

    /**
     * 重置所有参数并设置参数
     * @param [map] 地图
     */
    fun putAll(map: Map<String, Any?>): BodyHelper {
        this.map.clear()
        this.map.putAll(map)
        return this
    }

    /**
     * 添加参数
     * @param [name] 名字
     * @param [element] 元素
     */
    fun put(key: String, value: Any?): BodyHelper {
        map[key] = value
        return this
    }

    /**
     * 如果不存在，则放置
     * @param [key] 钥匙
     * @param [value] 价值
     * @return [BodyHelper]
     */
    fun putIfAbsent(key: String, value: Any?): BodyHelper {
        map.putIfAbsent(key, value)
        return this
    }

    /**
     * 核心方法，用于生成JsonObject，可接收一个lambda表达式来定制复杂类型转换为JsonElement的逻辑
     * @param converter 可选的转换函数，用于处理非基本类型数据转换为JsonElement的逻辑，默认为null
     * @return 构建好的JsonObject，可用于请求体等场景
     */
    fun body(converter: ((BodyHelper, Any?) -> JsonElement)? = null): JsonObject {
        val func = converter?.func() ?: elementConverter { _, it ->
            if (it == null) {
                return@elementConverter JsonNull.INSTANCE
            }
            return@elementConverter when (it) {
                is String -> JsonPrimitive(it)
                is Number -> JsonPrimitive(it.toString())
                is Boolean -> JsonPrimitive(it)
                is Char -> JsonPrimitive(it)
                is List<*> -> JsonArray().addPrimitiveElement(it)
                else -> convertPrimitiveElement(it, converter?.func())
            }
        }

        val obj = JsonObject()
        map.forEach { (t, u) ->
            try {
                obj.addElement(t, u, func)
            } catch (e: IllegalArgumentException) {
                // 记录详细的异常信息，包含键名和对应的值，方便排查问题
                logger.warning("Error adding element to JsonObject. Key: $t, Value: $u. Exception: ${e.message}")
                // 可以根据实际需求决定是否继续添加其他元素，或者直接抛出异常中断构建过程
            }
        }
        return obj
    }

    /**
     * 将元素添加到JsonObject中，根据元素的不同类型进行相应处理
     * @param key 要添加元素对应的键名
     * @param element 要添加的元素，可以是各种类型数据
     * @param converter 用于转换复杂类型为JsonElement的接口实现对象
     */
    private fun JsonObject.addElement(key: String, element: Any?, converter: JsonObjectConverter) {
        if (element == null) {
            add(key, element)
            return
        }
        add(key, converter.convert(element))
//        when (element) {
//            is List<*> -> {
//                val jsonArray = JsonArray()
//                for (item in element) {
//                    if (item != null) {
//                        try {
//                            jsonArray.add(convertPrimitiveElement(item, converter))
//                        } catch (e: IllegalArgumentException) {
//                            // 记录列表元素转换异常信息，包含列表中的元素值
//                            logger.warning("Error converting list element to JsonElement. Element: $item. Exception: ${e.message}")
//                            // 可以根据需求决定是否继续处理列表中的其他元素，比如跳过该异常元素继续处理下一个
//                        }
//                    }
//                }
//                this.add(key, jsonArray)
//            }
//            is String -> this.addProperty(key, element)
//            is Number -> this.addProperty(key, element)
//            is Boolean -> this.addProperty(key, element)
//            is Char -> this.addProperty(key, element)
//            else -> {
//                try {
//                    this.add(key, converter.convert(element))
//                } catch (e: IllegalArgumentException) {
//                    // 记录复杂类型转换异常信息，包含具体的复杂类型元素值
//                    logger.warning("Error converting complex element to JsonElement. Element: $element. Exception: ${e.message}")
//                    // 可以考虑返回一个默认的JsonElement来代替，避免整个请求体构建失败，具体取决于业务需求
//                }
//            }
//        }
    }

    /**
     * 转换基本元素为JsonElement，优先使用传入的自定义转换逻辑（如果有的话），否则使用默认转换逻辑
     * @param value 要转换的任意类型值
     * @param converter 可选的自定义转换逻辑，类型为JsonObjectConverter
     * @return 转换后的JsonElement
     */
    fun convertPrimitiveElement(value: Any?, converter: JsonObjectConverter? = null): JsonElement {
        if (value == null) {
            return JsonNull.INSTANCE
        }
        return converter?.convert(value) ?: when (value) {
            is String -> JsonPrimitive(value)
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Char -> JsonPrimitive(value)
            is HasJsonObject -> value.jsonObject()
            is List<*> -> JsonArray().addPrimitiveElement(value)
            else -> {
                logger.severe("Invalid element type for conversion. Element: $value")
                throw IllegalArgumentException("Invalid element, $value is not primitive or HasJsonObject or List!")
            }
        }
    }

    /**
     * 将元素添加到JsonArray中，根据元素类型进行相应处理，不符合要求的类型抛出异常
     * @param element 要添加的元素，可以是各种类型数据
     * @return 添加元素后的JsonArray
     */
    private fun JsonArray.addPrimitiveElement(element: Any?): JsonArray {
        if (element == null) {
            this.add(JsonNull.INSTANCE)
        }
        when (element) {
            is String -> this.add(element)
            is Number -> this.add(element)
            is Boolean -> this.add(element)
            is Char -> this.add(element)
            is HasJsonObject -> this.add(element.jsonObject())
            else -> {
                logger.severe("Invalid element type for adding to JsonArray. Element: $element")
                throw IllegalArgumentException("element is not primitive")
            }
        }
        return this
    }

    /**
     * 将可选的lambda表达式（类型为((Any) -> JsonElement)）转换为JsonObjectConverter接口实现对象，若lambda表达式为null则返回null
     * @param this lambda表达式本身，用于将Any类型转换为JsonElement类型
     * @return 转换后的JsonObjectConverter接口实现对象或者null
     */
    private fun ((BodyHelper, Any?) -> JsonElement)?.func(): JsonObjectConverter? =
        if (this == null) null
        else object : JsonObjectConverter {
            override fun convert(value: Any?): JsonElement {
                return this@func(this@BodyHelper, value)
            }
        }

    /**
     * 将传入的将Any类型转换为JsonElement的函数转换为JsonObjectConverter接口实现对象，确保返回非null的结果
     * @param value 传入的函数，用于执行类型转换逻辑
     * @return 转换后的JsonObjectConverter接口实现对象
     */
    private fun elementConverter(value: (BodyHelper, Any?) -> JsonElement): JsonObjectConverter =
        value.func()!!

    interface JsonObjectConverter {
        fun convert(value: Any?): JsonElement
    }

    interface HasJsonObject {
        fun jsonObject(): JsonElement
    }
}