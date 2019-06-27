# Horizontal Pod Autoscaler with application metrics

## Setup metrics server

- Create an EKS cluster:

	```
	eksctl create cluster \
		--name myeks \
		--node-type m5.2xlarge \
		--nodes 2 \
		--nodes-min 1 \
		--nodes-max 2
	```

- Install Helm:

	```
	kubectl -n kube-system create sa tiller
	kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
	helm init --service-account tiller
	```

- Deploy the metrics server:

	```
	helm install stable/metrics-server \
		--name metrics-server \
		--version 2.8.2 \
		--namespace metrics
	```

  Confirm the Metrics API is available:

  ```
  kubectl get apiservice v1beta1.metrics.k8s.io -o yaml
  NAME                     SERVICE                  AVAILABLE   AGE
	v1beta1.metrics.k8s.io   metrics/metrics-server   True        2m
  ```

- Check node resource usage:

	```
	kubectl top node
	NAME                                          CPU(cores)   CPU%   MEMORY(bytes)   MEMORY%   
	ip-192-168-2-219.us-west-2.compute.internal   39m          0%     383Mi           1%        
	ip-192-168-33-52.us-west-2.compute.internal   34m          0%     327Mi           1%    
	```

## Setup Prometheus

- Create namespace:

	```
	kubectl create namespace prometheus
	```

- Deploy Prometheus:

	```
	helm install stable/prometheus \
		--name prometheus \
		--namespace prometheus \
		--set alertmanager.persistentVolume.storageClass="gp2",server.persistentVolume.storageClass="gp2"
	```

	Verify pods are ready:

	```
	kubectl get pods -n prometheus
	NAME                                             READY   STATUS    RESTARTS   AGE
	prometheus-alertmanager-599df44bc8-t7bdl         2/2     Running   0          77s
	prometheus-kube-state-metrics-868c554d8c-qbwcw   1/1     Running   0          77s
	prometheus-node-exporter-2vvr6                   1/1     Running   0          77s
	prometheus-node-exporter-w2rb5                   1/1     Running   0          77s
	prometheus-pushgateway-56967b8d8f-bfccc          1/1     Running   0          77s
	prometheus-server-6cdd9b9884-qqxfn               2/2     Running   0          77s
	```

- Port forward:

	```
	kubectl --namespace=prometheus port-forward deploy/prometheus-server 9090 &
	```

## Deploy application

- Get application:

	```
	git clone https://github.com/arun-gupta/spring-boot-prometheus
	```

- Deploy application:

	```
	kubectl create -f k8s.yaml
	```

How to aggregate app metrics with Prometheus running in EKS?

## Setup HPA

- Custom metrics are not enabled by default:

	```
	kubectl get --raw /apis/custom.metrics.k8s.io/v1beta1  | jq
	Error from server (NotFound): the server could not find the requested resource
	```

- Deploy Prometheus operator, this will also enable custom metrics:

	```
	kubectl create -f https://raw.githubusercontent.com/luxas/kubeadm-workshop/master/demos/monitoring/custom-metrics.yaml
	namespace/custom-metrics created
	serviceaccount/custom-metrics-apiserver created
	clusterrolebinding.rbac.authorization.k8s.io/custom-metrics:system:auth-delegator created
	rolebinding.rbac.authorization.k8s.io/custom-metrics-auth-reader created
	clusterrole.rbac.authorization.k8s.io/custom-metrics-resource-reader created
	clusterrolebinding.rbac.authorization.k8s.io/custom-metrics-apiserver-resource-reader created
	clusterrole.rbac.authorization.k8s.io/custom-metrics-getter created
	clusterrolebinding.rbac.authorization.k8s.io/hpa-custom-metrics-getter created
	deployment.apps/custom-metrics-apiserver created
	service/api created
	apiservice.apiregistration.k8s.io/v1beta1.custom.metrics.k8s.io created
	clusterrole.rbac.authorization.k8s.io/custom-metrics-server-resources created
	clusterrolebinding.rbac.authorization.k8s.io/hpa-controller-custom-metrics created
	```

- Enable to `curl` the metrics using `kubectl`:

	```
	kubectl create clusterrolebinding allowall-cm --clusterrole custom-metrics-server-resources --user system:anonymous
	```

- Check custom metrics again:

	```
	kubectl get --raw /apis/custom.metrics.k8s.io/v1beta1  | jq
	```

- 