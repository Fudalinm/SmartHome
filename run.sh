./clear_images.sh
docker-compose down
./build_images.sh
sudo kill -9 $(sudo lsof -t -i:15672)
docker-compose up