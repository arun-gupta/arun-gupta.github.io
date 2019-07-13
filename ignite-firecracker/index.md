# Firecracker and Ignite

Let's create a [Ignite](https://github.com/weaveworks/ignite) VM using Firecracker.

- Create an Ubuntu 18.04 `c5.metal` instance
- Login to the instance:

	```
	ssh -i ~/.ssh/arun-us-west1.pem ubuntu@<ip-address>
	```

- Install Docker:

	```
	sudo apt-get update
	sudo apt-get docker.io -y
	```

- Install Ignite binary:

	```
	export VERSION=v0.4.1
	curl -fLo ignite https://github.com/weaveworks/ignite/releases/download/${VERSION}/ignite
	chmod +x ignite
	sudo mv ignite /usr/local/bin
	```

- Create a root shell:

	```
	sudo -s
	```

- Verify the version:

	```
	ignite version
	Ignite version: version.Info{Major:"0", Minor:"4", GitVersion:"v0.4.1", GitCommit:"32e40b90d89a0142368800282d800bfc56ee50ae", GitTreeState:"clean", BuildDate:"2019-07-12T17:26:40Z", GoVersion:"go1.12.1", Compiler:"gc", Platform:"linux/amd64"}
	Firecracker version: v0.17.0
	```

- Install Docker

	```
	apt-get install docker.io -y
	```

- Run new VM:

	```
	ignite run weaveworks/ignite-ubuntu \
		--name my-vm \
		--cpus 2 \
		--memory 1GB \
		--size 6GB \
		--ssh
	INFO[0000] Docker image "weaveworks/ignite-ubuntu:latest" not found locally, pulling... 
	INFO[0005] Starting image import...                     
	INFO[0009] Imported OCI image "weaveworks/ignite-ubuntu:latest" (220.5 MB) to base image with UID "b29e396a569681aa" 
	INFO[0009] Docker image "weaveworks/ignite-kernel:4.19.47" not found locally, pulling... 
	INFO[0011] Imported OCI image "weaveworks/ignite-kernel:4.19.47" (49.0 MB) to kernel image with UID "e8a03bd9c1f3d452" 
	INFO[0013] Created VM with ID "bc8fa3b71176d548" and name "my-vm" 
	INFO[0013] Pulling image "weaveworks/ignite:v0.4.1"...  
	INFO[0015] Started Firecracker VM "bc8fa3b71176d548" in a container with ID "1ba2c7919118e309de2197113292489ae9105de930139d46cc0de4109b05112d" 
	```

- List the VM:

	```
	ignite vm ls
	VM ID			IMAGE				KERNEL					CREATED	SIZE	CPUS	MEMORY		STATE	IPS		PORTS	NAME
	bc8fa3b71176d548	weaveworks/ignite-ubuntu:latest	weaveworks/ignite-kernel:4.19.47	46s ago	6.0 GB	2	1024.0 MB	Running	172.17.0.2	my-vm
	```

- SSH into the VM:

	```
	ignite ssh my-vm
	Welcome to Ubuntu 18.04.2 LTS (GNU/Linux 4.19.47 x86_64)

	 * Documentation:  https://help.ubuntu.com
	 * Management:     https://landscape.canonical.com
	 * Support:        https://ubuntu.com/advantage

	This system has been minimized by removing packages and content that are
	not required on a system that users do not log into.

	To restore this content, you can run the 'unminimize' command.

	The programs included with the Ubuntu system are free software;
	the exact distribution terms for each program are described in the
	individual files in /usr/share/doc/*/copyright.

	Ubuntu comes with ABSOLUTELY NO WARRANTY, to the extent permitted by
	applicable law.
	```

- Check CPUs:

	```
	root@bc8fa3b71176d548:~# lscpu | grep CPU
	CPU op-mode(s):      32-bit, 64-bit
	CPU(s):              2
	On-line CPU(s) list: 0,1
	CPU family:          6
	CPU MHz:             3000.012
	NUMA node0 CPU(s):   0,1
	```

- Check memory:

	```
	root@bc8fa3b71176d548:~# free -m
	              total        used        free      shared  buff/cache   available
	Mem:            990          34         913           0          42         864
	Swap:             0           0           0
	```

- Check disk space:

	```
	root@bc8fa3b71176d548:~# df -h
	Filesystem      Size  Used Avail Use% Mounted on
	/dev/root       5.7G  268M  5.1G   5% /
	devtmpfs        493M     0  493M   0% /dev
	tmpfs           496M     0  496M   0% /dev/shm
	tmpfs           496M  144K  496M   1% /run
	tmpfs           5.0M     0  5.0M   0% /run/lock
	tmpfs           496M     0  496M   0% /sys/fs/cgroup
	tmpfs           100M     0  100M   0% /run/user/0
	```
	