$(document).ready ->
  fetch('/api/recipes/versions').then (res) ->
    res.json().then (json) ->
      render(json)

render = (json) ->
  new Vue
    el: '#versions'
    data:
      versions: json
      ver: json[0]
      password: ''
      alert: null
    methods:
      deleteRecipe:  ->
        form = new FormData
        form.append('password', @password)
        fetch("/api/recipes/#{@ver}", {method: 'delete', body: form}).then (res) =>
          if res.status == 200
            location.reload(false)
          else
            res.text().then (text) =>
              @alert = text
