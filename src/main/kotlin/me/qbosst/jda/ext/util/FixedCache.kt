package me.qbosst.jda.ext.util

class FixedCache<K, V>
{
    private val map: MutableMap<K, V>
    private val _keys: Array<K>
    private var currentIndex: Int

    val size: Int
        get() = _keys.size

    val values: Collection<V>
        get() = map.values

    val keys: Set<K>
        get() = _keys.toSet()

    constructor(size: Int)
    {
        require(size >= 1) { "Cache size must at least be 1!" }

        this.map = mutableMapOf()
        this.currentIndex = 0

        @Suppress("UNCHECKED_CAST")
        this._keys = arrayOfNulls<Any>(size) as Array<K>
    }

    constructor(size: Int, cache: FixedCache<K, V>)
    {
        require(size >= 1) { "Cache size must at least be 1!" }
        require(cache.size <= size) { "New cache size must be bigger or equal to the old cache size!" }

        this.map = cache.map
        this._keys = cache._keys
        this.currentIndex = cache.currentIndex
    }

    fun put(key: K, value: V): V?
    {
        if(key in map)
            return map.put(key, value)

        if(_keys[currentIndex] != null)
            map.remove(_keys[currentIndex])

        _keys[currentIndex] = key
        currentIndex = (currentIndex + 1) % size
        return map.put(key, value)
    }

    fun put(key: K, value: V, consumer: (K, V) -> Unit): V?
    {
        if(key in map)
            return map.put(key, value)

        val (k, v) = _keys[currentIndex].let { current -> Pair(current, map.remove(current)) }

        _keys[currentIndex] = key
        currentIndex = (currentIndex + 1) % size

        val old = map.put(key, value)
        if(k != null && v != null)
            consumer.invoke(k, v)
        return old
    }

    fun pull(key: K): V? = map.remove(key)

    operator fun get(key: K): V? = map[key]

    operator fun contains(key: K): Boolean = map.containsKey(key)

    operator fun set(key: K, value: V): V? = put(key, value)

}