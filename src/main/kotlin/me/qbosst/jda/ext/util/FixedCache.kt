package me.qbosst.jda.ext.util

class FixedCache<K, V>
{
    private val _map: MutableMap<K, V>
    private val _keys: Array<K>
    private var currentIndex: Int

    val size: Int
        get() = _keys.size

    val values: Collection<V>
        get() = _map.values

    val keys: Set<K>
        get() = _keys.toSet()

    val map: Map<K, V>
        get() = _map

    constructor(size: Int)
    {
        require(size >= 1) { "Cache size must at least be 1!" }

        this._map = mutableMapOf()
        this.currentIndex = 0

        @Suppress("UNCHECKED_CAST")
        this._keys = arrayOfNulls<Any>(size) as Array<K>
    }

    constructor(size: Int, cache: FixedCache<K, V>)
    {
        require(size >= 1) { "Cache size must at least be 1!" }
        require(cache.size <= size) { "New cache size must be bigger or equal to the old cache size!" }

        this._map = cache._map
        this._keys = cache._keys
        this.currentIndex = cache.currentIndex
    }

    fun put(key: K, value: V): V?
    {
        if(key in _map)
            return _map.put(key, value)

        if(_keys[currentIndex] != null)
            _map.remove(_keys[currentIndex])

        _keys[currentIndex] = key
        currentIndex = (currentIndex + 1) % size
        return _map.put(key, value)
    }

    fun put(key: K, value: V, consumer: (K, V) -> Unit): V?
    {
        if(key in _map)
            return _map.put(key, value)

        val (k, v) = _keys[currentIndex].let { current -> Pair(current, _map.remove(current)) }

        _keys[currentIndex] = key
        currentIndex = (currentIndex + 1) % size

        val old = _map.put(key, value)
        if(k != null && v != null)
            consumer.invoke(k, v)
        return old
    }

    fun pull(key: K): V? = _map.remove(key)

    operator fun get(key: K): V? = _map[key]

    operator fun contains(key: K): Boolean = _map.containsKey(key)

    operator fun set(key: K, value: V): V? = put(key, value)

}