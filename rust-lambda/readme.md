# Lambda Function using Rust

## Create Rust Lambda Function

- Launch https://console.aws.amazon.com/ec2/v2/home#Images:visibility=public-images;search=amzn-ami-hvm-2017.03.1.20170812-x86_64-gp2[Lambda Exexcution Environment]
- Install and configure Rust:

  ssh -i <key> ec2-user@<ip-address>
  sudo yum -y update
  curl https://sh.rustup.rs -sSf | sh
  source $HOME/.cargo/env
  sudo yum groupinstall -y "Development Tools"

- Create placeholder Lambda function:

  mkdir lambda
  cd lambda
  cargo new my_lambda_function --bin
  cd my_lambda_function

- Update `Cargo.toml` to:
+
```
[package]
name = "my_lambda_function"
version = "0.1.0"
authors = ["Arun Gupta <your-email-id>"]
edition = "2018"
autobins = false

[dependencies]
lambda_runtime = "^0.1"
serde = "^1"
serde_json = "^1"
serde_derive = "^1"
log = "^0.4"
simple_logger = "^1"

[[bin]]
name = "bootstrap"
path = "src/main.rs"
```
+
- Edit `src/main.rs` to match code from the https://aws.amazon.com/blogs/opensource/rust-runtime-for-aws-lambda/[blog]
- Build the function:

  cargo build --release
  zip -j rust.zip ./target/release/bootstrap
  
## Deploy Rust Lambda Function

- Install SAM CLI

  sh -c "$(curl -fsSL https://raw.githubusercontent.com/Linuxbrew/install/master/install.sh)"
  export PATH=/home/linuxbrew/.linuxbrew/bin:$PATH
  brew upgrade
  brew update
  brew tap aws/tap
  brew install aws-sam-cli
  sam --version

- Create `template.yaml`:
+
```
AWSTemplateFormatVersion : '2010-09-09'
Transform: AWS::Serverless-2016-10-31
Description: Hello Rust Function

Resources:
  HelloRustFunction:
    Type: AWS::Serverless::Function
    Properties:
      FunctionName: HelloRust
      Handler: does.not.matter
      Runtime: provided
      MemorySize: 512
      CodeUri: rust.zip
```
+
- Configure AWS credentials

  mkdir ~/.aws
  vi ~/.aws/credentials # stick your credentials in this file
  aws configure
  
- Create a new or reuse an existing S3 bucket
- Package and deploy:

  sam package --template-file template.yaml --s3-bucket arun-sam-deployments --output-template-file ready.yaml
  sam deploy --template-file ready.yaml --stack-name HelloRust --capabilities CAPABILITY_IAM
  
## Run Rust Lambda Function

- Invoke the function:

  aws lambda invoke --function-name HelloRust --payload '{"firstName" : "Rustacean"}' output.json

- View the output:

  cat output.json
  {"message":"Hello, Rustacean!"}
