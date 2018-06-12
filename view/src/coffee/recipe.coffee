$(document).ready ->
  params = urlParameter()
  fetch('/api/recipes/versions').then (res) ->
    res.json().then (json) ->
      render(params.id, json)
  fetch("/api/item/#{params.id}").then (res) ->
    res.json().then (json) ->
      renderItem(json)

render = (itemId, versions) ->
  new Vue
    el:  '#recipe'
    data:
      items: []
      elems: []
      itemId: itemId
      version: versions[0]
      versions: versions
    methods:
      getJson: ->
        directs = _.concat(@items, @elems)
          .filter (x) -> x.direct
          .map (x) -> x.id
        param = queryRing(directs)
        fetch("/api/recipe/#{@version}/#{@itemId}#{param}").then (res) =>
          res.json().then (json) =>
            json = json.map (x) ->
              x.direct = _.includes(directs, x.id)
              x
            group = _.groupBy(json, (x) -> x.time != 0 && !x.direct && x.amount != x.exclude)
            @items = group[true]
            @elems = group[false]
      setDirect: (item) ->
        item.direct = true
        @getJson()
    mounted: ->
      @getJson()


renderItem = (json) ->
  new Vue
    el: '#item'
    data:
      item: json

queryRing = (xs) ->
  if xs.size == 0 then '' else '?' +
    (xs.map (x) -> "elem[]=#{x}").join('&')
