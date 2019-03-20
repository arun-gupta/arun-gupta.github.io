# Lambda Function using Rust

## Create Rust Lambda Function

- Launch [Lambda Exexcution Environment](https://console.aws.amazon.com/ec2/v2/home#Images:visibility=public-images;search=amzn-ami-hvm-2017.03.1.20170812-x86_64-gp2)
- Install and configure Rust:

  ```
  ssh -i ~/.ssh/arun-us-east1.pem ec2-user@ec2-34-205-177-254.compute-1.amazonaws.com
  sudo yum -y update
  curl https://sh.rustup.rs -sSf | sh
  ```

  Press ENTER

  ```
  source $HOME/.cargo/env
  sudo yum groupinstall -y "Development Tools"
  ```

- Create placeholder Lambda function:

  ```
  mkdir lambda
  cd lambda
  cargo new my_lambda_function --bin
  cd my_lambda_function
  ```

- Update `Cargo.toml` to:

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

- Edit `src/main.rs` to match code from the [blog](https://aws.amazon.com/blogs/opensource/rust-runtime-for-aws-lambda/). Here it is, for convenience:

  ```
  #[macro_use]
  extern crate lambda_runtime as lambda;
  #[macro_use]
  extern crate serde_derive;
  #[macro_use]
  extern crate log;
  extern crate simple_logger;

  use lambda::error::HandlerError;

  use std::error::Error;

  #[derive(Deserialize, Clone)]
  struct CustomEvent {
      #[serde(rename = "firstName")]
      first_name: String,
  }

  #[derive(Serialize, Clone)]
  struct CustomOutput {
      message: String,
  }

  fn main() -> Result<(), Box<dyn Error>> {
      simple_logger::init_with_level(log::Level::Info)?;
      lambda!(my_handler);

      Ok(())
  }

  fn my_handler(e: CustomEvent, c: lambda::Context) -> Result<CustomOutput, HandlerError> {
      if e.first_name == "" {
          error!("Empty first name in request {}", c.aws_request_id);
          return Err(c.new_error("Empty first name"));
      }

      Ok(CustomOutput {
          message: format!("Hello, {}!", e.first_name),
      })
  }
  ```

  First of all, we import all the needed crates. We import `lambda_runtime` crate that is published at crates.io and is specified in `Cargo.toml`. `serde_derive` crate generates serializer and deserializer, and the respective functions are annotated accordingly.

  `main()` method is the entrypoint for bootable executable. In this method, we use `lambda!()` macro in the `lambda_runtime` crate to bootstrap our custom runtime. In its most basic form, the macro takes a pointer to the handler function defined in your code. 

  The handler function, `my_handler`, receives an event object that implements the `serde::Deserialize` trait, `CustomEvent` in our case. The custom runtime also generates a `Context` object for each event and passes it to the handler. 

  The return value is `Result` with a custom output type that implements the `serde::Serialize`, `CustomOutput` in our case.

- Build the function:

  ```
  cargo build --release
  zip -j rust.zip ./target/release/bootstrap
  ```
  
## Deploy Rust Lambda Function

- Install SAM CLI

  ```
  sh -c "$(curl -fsSL https://raw.githubusercontent.com/Linuxbrew/install/master/install.sh)"
  ```

  Press ENTER to continue

  ```
  export PATH=/home/linuxbrew/.linuxbrew/bin:$PATH
  brew upgrade
  brew update
  brew tap aws/tap
  brew install aws-sam-cli
  sam --version
  ```

- Create `template.yaml`:

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

- Configure AWS credentials

  ```
  mkdir ~/.aws
  vi ~/.aws/credentials # stick your credentials in this file
  aws configure
  ```
  
- Create a new or reuse an existing S3 bucket
- Package and deploy:

  ```
  sam package --template-file template.yaml --s3-bucket arun-sam-deployments --output-template-file ready.yaml
  sam deploy --template-file ready.yaml --stack-name HelloRust --capabilities CAPABILITY_IAM
  ```
  
## Run Rust Lambda Function

- Invoke the function:

  ```
  aws lambda invoke --function-name HelloRust --payload '{"firstName" : "Rustacean"}' output.json
  ```

- View the output:

  ```
  cat output.json
  {"message":"Hello, Rustacean!"}
  ```

