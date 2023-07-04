from node
run npm install -g npm-link
copy . /opt
workdir /opt
run npm link
run mkdir /output
workdir /output