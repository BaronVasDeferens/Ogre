#!bin/bash
echo | mailx -s "OGRE: $2 has finished a turn (#$3)" $1 > /dev/null
echo notification sent to $1
