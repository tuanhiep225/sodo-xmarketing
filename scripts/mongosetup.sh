#!/bin/bash

mongodb=`getent hosts ${MONGODB} | awk '{ print $1 }'`

port=${PORT:-27018}

echo "Waiting for startup.."
until mongo --host ${mongodb}:${port} --eval 'quit(db.runCommand({ ping: 1 }).ok ? 0 : 2)' &>/dev/null; do
  printf '.'
  sleep 1
done

echo "Started.."

echo SETUP.sh time now: `date +"%T" `
mongo --host ${mongodb}:${port} <<EOF
   var cfg = {
        "_id": "rs0",
        "version": 1,
        "members": [
            {
                "_id": 0,
                "host": "${mongodb}:27018"
            }
        ]
    };
    rs.initiate(cfg, { force: true });
    rs.reconfig(cfg, { force: true });