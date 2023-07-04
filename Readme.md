# Boilerplate Pipeline

This repository contains the source code for the **Boilerplate Pipeline** project. It provides instructions on how to build and run the project locally, as well as how to build and run it using Docker.

## Prerequisites

Before proceeding, ensure that you have the following installed on your system:

- Node.js
- Docker (optional)

## Build and Run Locally

To build and run the project locally, follow these steps:

1. Clone this repository to your local machine.
   ```bash
   git clone https://code.ariefwara.com/boilerplate/pipeline.git
   ```

2. Navigate to the project directory.
   ```bash
   cd pipeline
   ```

3. Install the project dependencies.
   ```bash
   npm install
   ```

4. Create a symbolic link to make the project executable globally.
   ```bash
   npm link
   ```

5. Run the project with the desired parameters.
   ```bash
   generate --type=dev --framework=spring --name=xxx
   ```
   Replace `--type`, `--framework`, and `--name` with your desired values.

## Build and Run with Docker

To build and run the project using Docker, follow these steps:

1. Clone this repository to your local machine (if you haven't done so already).
   ```bash
   git clone https://code.ariefwara.com/boilerplate/pipeline.git
   ```

2. Navigate to the project directory.
   ```bash
   cd pipeline
   ```

3. Build the Docker image.
   ```bash
   docker build -t boilerplate/pipeline .
   ```

4. Run the Docker container, mapping the current directory to the `/output` directory inside the container, and execute the project with the desired parameters.
   ```bash
   docker run --rm -v \"C:\\ProgramData\\Jenkins\\.jenkins\\jobs\":/output boilerplate/pipeline generate --build_directory=${params.build_directory} --name=${params.name} --type=${params.type} --framework=${params.framework} --repo_project=${params.repo_project} --branch=${params.branch} --ip_nexus_package=${params.ip_nexus_package} --ip_nexus_image=${params.ip_nexus_image} --repo_cicd=${params.repo_cicd} --output=${params.output}
   ```
   Replace `--type`, `--framework`, and `--name` with your desired values.

## Additional Information

- The command `npm install` installs the project dependencies specified in the `package.json` file.
- The command `npm link` creates a global symlink for the project, allowing you to run it from any directory.
- The `generate` command is used to run the project with the specified parameters. Adjust the parameters as needed for your specific use case.
- The Docker commands build a Docker image for the project and run it in a container. The `-v` flag maps the current directory to the `/output` directory inside the container, allowing the generated files to be accessible on your local machine. Adjust the Docker commands as needed for your specific use case.

- for node running example 
```bash
node index.js --type=development --framework=test --name=HelloWorld --repo=https://github.com/ihsbramn/hello-world.git --version=lts
```
## License

This project is licensed under the [MIT License](LICENSE).