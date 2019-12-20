import os
import sys

woodname = raw_input("What is the name of your wood? ")
woodname = woodname.lower()

if woodname == "redwood":
    sys.exit(0)

def create(name):
    f = open(name + ".json", "w+")
    f2 = open("conveyor.json", "r")
    contents = f2.read()
    new_blockstate = contents.replace("conveyor", name)
    f.write(new_blockstate)
    f.close()
    return;

create(woodname)
