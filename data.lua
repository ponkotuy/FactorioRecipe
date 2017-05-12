array = {}
data = {}
function data:extend(xs)
  for i, x in pairs(xs) do
    table.insert(array, x)
  end
end
