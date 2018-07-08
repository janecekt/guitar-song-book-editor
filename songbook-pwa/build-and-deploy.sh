#!/bin/bash

cd ~/workspace/guitar-song-book-editor/songbook-pwa \
    && echo -e '\n\nBuilding application\n\n' \
    && docker run --net=host -v ~/workspace/guitar-song-book-editor/songbook-pwa/:/mnt -w /mnt/client alpine-node-chromium:latest yarn build \
    && echo -e '\n\nDeploying application\n\n' \
    && rsync -avz --delete ./target/dist/ material:~/links/www/songbook