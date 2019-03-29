# Install Helm on Amazon EKS

- Install/upgrade Helm

  ```
  brew install kubernetes-helm` or `brew upgrade kubernetes-helm
  ```

- Install Tiller:

  ```
  kubectl -n kube-system create sa tiller
  kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
  helm init --service-account tiller
  ```