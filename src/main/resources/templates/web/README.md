# My Application
Basic website generated from CBR Form Designer.

# Prerequisites
To run with Docker and docker-compose, please make sure that:
- Docker
- docker-compose

is installed in your machine. If you're using Windows, you can use [Docker Toolbox](https://docs.docker.com/toolbox/toolbox_install_windows/) to run docker and put this application folder in `C://Users//Public//`.

To run the application without Docker, please install all the required application.
- [NodeJS (> v8)](https://nodejs.org/en/download/)
- [MySQL (> v5)](https://www.apachefriends.org/download.html)
- A web browser (Chrome, Firefox, or Edge is recommended)

# Start The Application With Docker
Open the Docker Terminal if you are in Windows or the default terminal if you are using Linux/Mac. Make sure the docker-machine is turned on.
1. Build the images
```sh
docker-compose build
```
2. Make sure you don't have shut down all previous containers.
```sh
docker-compose down -v
```
3. Spin up the containers.
```
docker-compose up
```
4. Open your browser on `localhost:3000` if you are on Linux/Mac. If you are on Windows, look for your Docker VM host by executing `docker-machine env`. The application is running on your `DOCKER_HOST` port `3000`.

# Start The Application Without Docker
Open a terminal or command prompt and follow these instructions.
1. Install dependencies
```sh
npm install
```
2. Make an environment file named `.env` on the root project directory. Here is an example of a `.env` file.
```
MODE=production
DB_HOST=db
DB_USER=admin
DB_PASSWORD=password
DB_DATABASE=application
```
3. Start the application
```sh
npm start
```
4. Open a web browser and go to [http://localhost:3000](http://localhost:3000).
