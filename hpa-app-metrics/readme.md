# Horizontal Pod Autoscaler with application metrics

## Setup EKS

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

## Install metrics server

This is needed to generate resource metrics.

- Deploy the metrics server:

	```
	helm install stable/metrics-server \
		--name metrics-server \
		--version 2.8.2 \
		--namespace metrics
	```

  Confirm the Metrics API is available:

  ```
  kubectl get --raw /apis/metrics.k8s.io/v1beta1  | jq
	{
	  "kind": "APIResourceList",
	  "apiVersion": "v1",
	  "groupVersion": "metrics.k8s.io/v1beta1",
	  "resources": [
	    {
	      "name": "nodes",
	      "singularName": "",
	      "namespaced": false,
	      "kind": "NodeMetrics",
	      "verbs": [
	        "get",
	        "list"
	      ]
	    },
	    {
	      "name": "pods",
	      "singularName": "",
	      "namespaced": true,
	      "kind": "PodMetrics",
	      "verbs": [
	        "get",
	        "list"
	      ]
	    }
	  ]
	}
  ```

  Get more metrics about nodes:

  ```
  kubectl get --raw /apis/metrics.k8s.io/v1beta1/nodes  | jq
  ```

  Get more details about pods:

  ```
  kubectl get --raw /apis/metrics.k8s.io/v1beta1/pods  | jq
  ```

- Check node resource usage:

	```
	kubectl top node
	NAME                                          CPU(cores)   CPU%   MEMORY(bytes)   MEMORY%   
	ip-192-168-2-219.us-west-2.compute.internal   39m          0%     383Mi           1%        
	ip-192-168-33-52.us-west-2.compute.internal   34m          0%     327Mi           1%    
	```

## Install Prometheus adapter

This is needed to generate custom metrics that will be used for HPA.

- Deploy Prometheus adapter to enable custom metrics:

	```
	helm install --name prometheus-adapter stable/prometheus-adapter -n prometheus-adapter
	```

- Check custom metrics:

	```
	kubectl get --raw /apis/custom.metrics.k8s.io/v1beta1  | jq
	{
	  "kind": "APIResourceList",
	  "apiVersion": "v1",
	  "groupVersion": "custom.metrics.k8s.io/v1beta1",
	  "resources": []
	}
	```

## Install Prometheus

- Install Prometheus:

	```
	helm install stable/prometheus \
		--name prometheus \
		--namespace prometheus \
		--set alertmanager.persistentVolume.storageClass="gp2",server.persistentVolume.storageClass="gp2"
	```

  Verify:

  ```
  kubectl get pods -n prometheus
  ```

- Port forward:

	```
	kubectl --namespace=prometheus port-forward deploy/prometheus-server 9090
	```

	Access [Prometheus Console](localhost:9090)

## Deploy application

This application generates custom metrics.

- Get application:

	```
	git clone https://github.com/arun-gupta/spring-boot-prometheus
	```

- Deploy application:

	```
	helm install --name myapp chart
	```

- Access the application:

	```
	ENDPOINT=$(kubectl get svc/myapp-greeting -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
	curl http://$ENDPOINT/hello
	```

- Access metrics

	```
	curl http://$ENDPOINT/actuator/prometheus
	```

## Setup Grafana

- Install Grafana:

	```
	helm install stable/grafana \
		--name grafana \
		--namespace grafana
	```

- Port forward:

	```
	```

## Setup HPA

- Create Horizontal Pod Autoscaler:

	```
	kubectl create -f hpa.yaml
	```

	HPA should read the metrics `http_server_requests_seconds_max{uri="/hello"}`. How would it?

## Generate traffic

- Download traffic generator:

	```
	curl -o hey https://storage.googleapis.com/jblabs/dist/hey_darwin_v0.1.2
	```

- Monitor latency of HTTP requests to '/hello':

	```
	watch -n 1 -d "curl http://$ENDPOINT/actuator/prometheus | grep http_server_requests_seconds_max | grep hello"
	```

- Generate traffic:

	```
	hey_darwin_v0.1.2 -c 100 -n 5000 -m GET http://$ENDPOINT/hello
	```

