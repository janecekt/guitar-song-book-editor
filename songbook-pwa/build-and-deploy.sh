#!/bin/bash

cd ~/workspace/guitar-song-book-editor/songbook-pwa \
    && echo -e '\n\nBuilding application\n\n' \
    && docker run --net=host --user 1000 -v ~/workspace/guitar-song-book-editor/songbook-pwa/:/mnt -w /mnt/client alpine-node-chromium:latest yarn build \
    && echo -e '\n\nDeploying application\n\n' \
    && cp ~/workspace/guitar-song-book-editor-songs/pdf/song-book.pdf ./target/dist/ \
    && rsync -avz --delete ./target/dist/ material:~/links/www/songbook   
