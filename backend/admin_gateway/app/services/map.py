"""Shared service map used by router and service modules."""
# isame underscore(_) kaam nahi kar raha to service name me to isame slash(-) lagana hoga
SERVICE_MAP = {
    "auth": "http://127.0.0.1:8003",

}
#   kabhi bhi name likhane se pahale chack kare ki kya app jo
#  api me name use kar raha hai oo  same hai ya nahi jaise 
# "group-chat/create-group" agar app use kar raha hai to yaha par "group-chat": "http://127.0.0.1:8004" hi
#  hona chaiye aisa n ho ko yaha par group sirf likh de ya app bhej raha ho group-chat 