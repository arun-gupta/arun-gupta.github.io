# IntelliJ and AWS Toolkit Demo Script

## Pre-reqs

```
IntelliJ IDEA 2018.3.5 (Community Edition)
Build #IC-183.5912.21, built on February 26, 2019
JRE: 1.8.0_152-release-1343-b28 x86_64
JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
macOS 10.13.6
```

## Install SAM CLI

Needs SAM CLI 0.13.0

```
brew install aws-sam-cli
```

or

```
brew upgrade aws-sam-cli
```

Check the version:

```
sam --version
SAM CLI, version 0.12.0
```

## Setup Plugin

- IntelliJ IDEA, Preferences
- Subscribe to EAP builds as explained at https://github.com/aws/aws-toolkit-jetbrains#eap-builds
- Search for `plugins`
- Search for `aws tooklkit`, select. Verify the version number is `v1.2-EAP.2019.03.14` at least and click on `Install`.
- Click on `Restart IDE` to restart the IDE

## Create new application

- `Create New Project`
- `AWS`, `AWS Serverless Application`, `Next`
- Check the image intellij-hello-serverless.png

## Show project

- Expand project, `HelloWorldFunction`, `src`, `main`, `java`, `helloworld`
- Explain `App.java`, standard Java imports and AWS SDK

## Deploy Function

- Click on `AWS Explorer`
- Configure AWS connection settings
  - Talk about profile and default region. TODO: During first run, are these picked up from local machine?
- Pick the region `us-west-1`
- Right click on `Lambda`, select `Create new AWS Lambda...`
  - Show code completion in `Handler:` box
  - Change memory to 1088
  - CLick on `Create`
