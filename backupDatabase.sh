#!/bin/bash
folderName=deploy-`date +"%m-%d-%y_%H-%M-%S"`;

echo "Creating directory /opt/$folderName ...";
mkdir /opt/$folderName;
echo "Created directory /opt/$folderName";
echo "Being backup database";
echo "Backing up LikeOrder ...";
mongodump --db likeorder --out /opt/$folderName/likeorder;
echo "Backed up LikeOrder";
echo "Backing up Sod ...";
mongodump --db sod --out /opt/$folderName/sod;
echo "Backed up Sod";
echo "Backing up Xorder ...";
mongodump --db xorder --out /opt/$folderName/xorder;
echo "Backed up Xorder";
echo "Compressing backup folder ...";
cd /opt/;
zip -r $folderName.zip $folderName/;
echo "Compressed backup folder";
drive upload --delete /opt/$folderName.zip;
echo "Removing backup folder ...";
rm -rf /opt/$folderName;
echo "Backup completed";
exit 0;