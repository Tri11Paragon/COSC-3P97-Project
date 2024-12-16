
str=(-H "content-type: application/json" -X POST localhost:8080/api/db/)

echo "deleting"
curl "${str[@]}delete_user" --data '"hewwow"' 
echo
curl "${str[@]}delete_user" --data '"hewwow2"' 
echo
curl "${str[@]}delete_user" --data '"hewwow3"' 
echo

echo
echo "creating"
curl "${str[@]}create_user" -X POST --data '{"id": "hewwow", "name": "meow"}' 
echo
curl "${str[@]}create_user" -X POST --data '{"id": "hewwow2", "name": "nyaa"}'
echo
curl "${str[@]}create_user" --data '{"id": "hewwow3", "name": "lfkjfkljas"}'
echo

echo
echo "get"
curl "${str[@]}get_user" --data '"hewwow"'
echo

echo
echo "update"
curl "${str[@]}update_user" --data '{"id": "hewwow", "name": "meow updated"}'
echo

echo
echo "get"
curl "${str[@]}get_user" --data '"hewwow"'
echo

echo
echo "creating walk"
w1=$(curl --silent "${str[@]}create_walk" --data '{"user_id": "hewwow", "walk": {"start": 0, "end": 0, "name": "howard", "rating": 0.9}, "conditions": []}')
echo $w1
w2=$(curl --silent "${str[@]}create_walk" --data '{"user_id": "hewwow", "walk": {"start": 0, "end": 0, "name": "asdfjkghaow8ry", "rating": 0.9}, "conditions": [{"time": 2, "lon": 2, "lat": 2, "conditions": {}}]}')
echo $w2
echo

echo
echo "get walks"
curl "${str[@]}list_walks" --data '"hewwow"'
echo


echo
echo "list walk info"
curl "${str[@]}list_walk_info" --data "{\"user_id\": \"hewwow\", \"walk_id\": $w1}"
echo
curl "${str[@]}list_walk_info" --data "{\"user_id\": \"hewwow\", \"walk_id\": $w2}"
echo


echo
echo "updating walk"
curl "${str[@]}update_walk" --data "{\"user_id\": \"hewwow\", \"walk_id\": $w1, \"rating\": 22222}"
echo

echo
echo "get walks"
curl "${str[@]}list_walks" --data '"hewwow"'
echo


echo
echo "delete walk"
curl "${str[@]}delete_walk" --data "{\"user_id\": \"hewwow\", \"walk_id\": $w1}"
echo

echo
echo "get walks"
curl "${str[@]}list_walks" --data '"hewwow"'
echo