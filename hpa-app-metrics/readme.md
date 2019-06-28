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

- Check node resource usage:

	```
	kubectl top node
	NAME                                          CPU(cores)   CPU%   MEMORY(bytes)   MEMORY%   
	ip-192-168-2-219.us-west-2.compute.internal   39m          0%     383Mi           1%        
	ip-192-168-33-52.us-west-2.compute.internal   34m          0%     327Mi           1%    
	```

## Deploy Prometheus

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

  Verify:

  ```
  kubectl get pods -n prometheus
  NAME                                             READY   STATUS    RESTARTS   AGE
	prometheus-alertmanager-599df44bc8-6f7zq         1/2     Running   0          22s
	prometheus-kube-state-metrics-868c554d8c-59hmm   1/1     Running   0          22s
	prometheus-node-exporter-9mqdk                   1/1     Running   0          22s
	prometheus-node-exporter-rm5r5                   1/1     Running   0          22s
	prometheus-pushgateway-56967b8d8f-r8fpz          1/1     Running   0          22s
	prometheus-server-6cdd9b9884-dfqwn               1/2     Running   0          22s
  ```

- Port forward:

	```
	kubectl --namespace=prometheus port-forward deploy/prometheus-server 9090
	```

	Access [Prometheus console](http://localhost:9090).

## Deploy application

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
	ENDPOINT=$(kubectl get svc/myapp-greeting \
		-o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
	curl http://$ENDPOINT/hello
	```

- Access metrics

	```
	curl http://$ENDPOINT/actuator/prometheus
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
	helm install --name my-release stable/prometheus-adapter
	```

- Check custom metrics again:

	```
	kubectl get --raw /apis/custom.metrics.k8s.io/v1beta1  | jq
	{
	  "kind": "APIResourceList",
	  "apiVersion": "v1",
	  "groupVersion": "custom.metrics.k8s.io/v1beta1",
	  "resources": []
	}
	```

- 