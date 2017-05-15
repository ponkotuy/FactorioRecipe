@urlParameter = ->
  fromURLParameter(location.search.slice(1))

@fromURLParameter = (str) ->
  obj = {}
  for kv in str.split('&')
    ary = kv.split('=')
    key = ary.shift()
    obj[key] = ary.join('=')
  obj
