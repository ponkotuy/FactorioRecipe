$(document).ready ->
  params = urlParameter()
  render(params.id)
  fetch("/api/item/#{params.id}").then (res) ->
    res.json().then (json) ->
      renderItem(json)

render = (itemId) ->
  new Vue
    el:  '#recipe'
    data:
      items: []
      elems: []
      itemId: itemId
    methods:
      getJson: ->
        directs = _.concat(@items, @elems)
          .filter (x) -> x.direct
          .map (x) -> x.id
        param = queryRing(directs)
        fetch("/api/recipe/#{itemId}#{param}").then (res) =>
          res.json().then (json) =>
            json = json.map (x) ->
              x.direct = _.includes(directs, x.id)
              x
            console.log(json.map (x) -> x.direct)
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
