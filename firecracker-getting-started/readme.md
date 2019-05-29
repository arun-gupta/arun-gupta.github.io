# Create Firecracker microVM using firectl

- Create a `m5d.metal` Ubuntu instance
- Login to the instance:

	```
	ssh -i ~/.ssh/arun-us-east1.pem ubuntu@ec2-34-207-78-53.compute-1.amazonaws.com
	```

- Get firectl binary:

	```
	curl -oL firectl https://firectl-release.s3.amazonaws.com/firectl-v0.1.0
	chmod +x firectl
	```

- Get Firecracker binary:

	```
	curl -Lo firecracker https://github.com/firecracker-microvm/firecracker/releases/download/v0.16.0/firecracker-v0.16.0
	chmod +x firecracker
	sudo mv firecracker /usr/local/bin/firecracker
	```

- Give read/write access to KVM:

	```
	sudo setfacl -m u:${USER}:rw /dev/kvm
	```

- Download kernel and root filesystem:

	```
	curl -fsSL -o hello-vmlinux.bin https://s3.amazonaws.com/spec.ccfc.min/img/hello/kernel/hello-vmlinux.bin
	curl -fsSL -o hello-rootfs.ext4 https://s3.amazonaws.com/spec.ccfc.min/img/hello/fsfiles/hello-rootfs.ext4
	```

- Create microVM:

	```
	./firectl \
    --kernel=hello-vmlinux.bin \
    --root-drive=hello-rootfs.ext4
	```

	This boots the kernel using the filesytem:

	```
	ubuntu@ip-172-31-16-227:~$ ./firectl   --kernel=hello-vmlinux.bin   --root-drive=hello-rootfs.ext4
	INFO[0000] Called startVMM(), setting up a VMM on /home/ubuntu/.firecracker.sock-4780-81 
	INFO[0000] VMM logging and metrics disabled.            
	INFO[0000] refreshMachineConfig: [GET /machine-config][200] getMachineConfigOK  &{CPUTemplate:Uninitialized HtEnabled:true MemSizeMib:512 VcpuCount:1} 
	INFO[0000] PutGuestBootSource: [PUT /boot-source][204] putGuestBootSourceNoContent  
	INFO[0000] Attaching drive hello-rootfs.ext4, slot 1, root true. 
	INFO[0000] Attached drive hello-rootfs.ext4: [PUT /drives/{drive_id}][204] putGuestDriveByIdNoContent  
	INFO[0000] startInstance successful: [PUT /actions][204] createSyncActionNoContent  
	[    0.000000] Linux version 4.14.55-84.37.amzn2.x86_64 (mockbuild@ip-10-0-1-79) (gcc version 7.3.1 20180303 (Red Hat 7.3.1-5) (GCC)) #1 SMP Wed Jul 25 18:47:15 UTC 2018
	[    0.000000] Command line: ro console=ttyS0 noapic reboot=k panic=1 pci=off nomodules  root=/dev/vda virtio_mmio.device=4K@0xd0000000:5
	[    0.000000] x86/fpu: Supporting XSAVE feature 0x001: 'x87 floating point registers'
	[    0.000000] x86/fpu: Supporting XSAVE feature 0x002: 'SSE registers'
	[    0.000000] x86/fpu: Supporting XSAVE feature 0x004: 'AVX registers'
	[    0.000000] x86/fpu: Supporting XSAVE feature 0x008: 'MPX bounds registers'
	[    0.000000] x86/fpu: Supporting XSAVE feature 0x010: 'MPX CSR'
	[    0.000000] x86/fpu: Supporting XSAVE feature 0x020: 'AVX-512 opmask'
	[    0.000000] x86/fpu: Supporting XSAVE feature 0x040: 'AVX-512 Hi256'
	[    0.000000] x86/fpu: Supporting XSAVE feature 0x080: 'AVX-512 ZMM_Hi256'
	[    0.000000] x86/fpu: Supporting XSAVE feature 0x200: 'Protection Keys User registers'
	[    0.000000] x86/fpu: xstate_offset[2]:  576, xstate_sizes[2]:  256
	[    0.000000] x86/fpu: xstate_offset[3]:  832, xstate_sizes[3]:   64
	[    0.000000] x86/fpu: xstate_offset[4]:  896, xstate_sizes[4]:   64
	[    0.000000] x86/fpu: xstate_offset[5]:  960, xstate_sizes[5]:   64
	[    0.000000] x86/fpu: xstate_offset[6]: 1024, xstate_sizes[6]:  512
	[    0.000000] x86/fpu: xstate_offset[7]: 1536, xstate_sizes[7]: 1024
	[    0.000000] x86/fpu: xstate_offset[9]: 2560, xstate_sizes[9]:    8
	[    0.000000] x86/fpu: Enabled xstate features 0x2ff, context size is 2568 bytes, using 'compacted' format.
	[    0.000000] e820: BIOS-provided physical RAM map:
	[    0.000000] BIOS-e820: [mem 0x0000000000000000-0x000000000009fbff] usable
	[    0.000000] BIOS-e820: [mem 0x0000000000100000-0x000000001fffffff] usable
	[    0.000000] NX (Execute Disable) protection: active
	[    0.000000] DMI not present or invalid.
	[    0.000000] Hypervisor detected: KVM
	[    0.000000] tsc: Using PIT calibration value
	[    0.000000] e820: last_pfn = 0x20000 max_arch_pfn = 0x400000000
	[    0.000000] MTRR: Disabled
	[    0.000000] x86/PAT: MTRRs disabled, skipping PAT initialization too.
	[    0.000000] CPU MTRRs all blank - virtualized system.
	[    0.000000] x86/PAT: Configuration [0-7]: WB  WT  UC- UC  WB  WT  UC- UC  
	[    0.000000] found SMP MP-table at [mem 0x0009fc00-0x0009fc0f] mapped at [ffffffffff200c00]
	[    0.000000] Scanning 1 areas for low memory corruption
	[    0.000000] Using GB pages for direct mapping
	[    0.000000] No NUMA configuration found
	[    0.000000] Faking a node at [mem 0x0000000000000000-0x000000001fffffff]
	[    0.000000] NODE_DATA(0) allocated [mem 0x1ffde000-0x1fffffff]
	[    0.000000] kvm-clock: Using msrs 4b564d01 and 4b564d00
	[    0.000000] kvm-clock: cpu 0, msr 0:1ffdc001, primary cpu clock
	[    0.000000] kvm-clock: using sched offset of 70374135 cycles
	[    0.000000] clocksource: kvm-clock: mask: 0xffffffffffffffff max_cycles: 0x1cd42e4dffb, max_idle_ns: 881590591483 ns
	[    0.000000] Zone ranges:
	[    0.000000]   DMA      [mem 0x0000000000001000-0x0000000000ffffff]
	[    0.000000]   DMA32    [mem 0x0000000001000000-0x000000001fffffff]
	[    0.000000]   Normal   empty
	[    0.000000] Movable zone start for each node
	[    0.000000] Early memory node ranges
	[    0.000000]   node   0: [mem 0x0000000000001000-0x000000000009efff]
	[    0.000000]   node   0: [mem 0x0000000000100000-0x000000001fffffff]
	[    0.000000] Initmem setup node 0 [mem 0x0000000000001000-0x000000001fffffff]
	[    0.000000] Intel MultiProcessor Specification v1.4
	[    0.000000] MPTABLE: OEM ID: FC      
	[    0.000000] MPTABLE: Product ID: 000000000000
	[    0.000000] MPTABLE: APIC at: 0xFEE00000
	[    0.000000] Processor #0 (Bootup-CPU)
	[    0.000000] IOAPIC[0]: apic_id 2, version 17, address 0xfec00000, GSI 0-23
	[    0.000000] Processors: 1
	[    0.000000] smpboot: Allowing 1 CPUs, 0 hotplug CPUs
	[    0.000000] PM: Registered nosave memory: [mem 0x00000000-0x00000fff]
	[    0.000000] PM: Registered nosave memory: [mem 0x0009f000-0x000fffff]
	[    0.000000] e820: [mem 0x20000000-0xffffffff] available for PCI devices
	[    0.000000] Booting paravirtualized kernel on KVM
	[    0.000000] clocksource: refined-jiffies: mask: 0xffffffff max_cycles: 0xffffffff, max_idle_ns: 7645519600211568 ns
	[    0.000000] random: get_random_bytes called from start_kernel+0x94/0x486 with crng_init=0
	[    0.000000] setup_percpu: NR_CPUS:128 nr_cpumask_bits:128 nr_cpu_ids:1 nr_node_ids:1
	[    0.000000] percpu: Embedded 41 pages/cpu @ffff88001fc00000 s128728 r8192 d31016 u2097152
	[    0.000000] KVM setup async PF for cpu 0
	[    0.000000] kvm-stealtime: cpu 0, msr 1fc15040
	[    0.000000] PV qspinlock hash table entries: 256 (order: 0, 4096 bytes)
	[    0.000000] Built 1 zonelists, mobility grouping on.  Total pages: 128905
	[    0.000000] Policy zone: DMA32
	[    0.000000] Kernel command line: ro console=ttyS0 noapic reboot=k panic=1 pci=off nomodules  root=/dev/vda virtio_mmio.device=4K@0xd0000000:5
	[    0.000000] PID hash table entries: 2048 (order: 2, 16384 bytes)
	[    0.000000] Memory: 498120K/523896K available (8204K kernel code, 622K rwdata, 1464K rodata, 1268K init, 2820K bss, 25776K reserved, 0K cma-reserved)
	[    0.000000] SLUB: HWalign=64, Order=0-3, MinObjects=0, CPUs=1, Nodes=1
	[    0.000000] Kernel/User page tables isolation: enabled
	[    0.004000] Hierarchical RCU implementation.
	[    0.004000] 	RCU restricting CPUs from NR_CPUS=128 to nr_cpu_ids=1.
	[    0.004000] RCU: Adjusting geometry for rcu_fanout_leaf=16, nr_cpu_ids=1
	[    0.004000] NR_IRQS: 4352, nr_irqs: 48, preallocated irqs: 16
	[    0.004000] Console: colour dummy device 80x25
	[    0.004000] console [ttyS0] enabled
	[    0.004000] tsc: Detected 2500.010 MHz processor
	[    0.004000] Calibrating delay loop (skipped) preset value.. 5000.02 BogoMIPS (lpj=10000040)
	[    0.004000] pid_max: default: 32768 minimum: 301
	[    0.004000] Security Framework initialized
	[    0.004000] SELinux:  Initializing.
	[    0.004000] Dentry cache hash table entries: 65536 (order: 7, 524288 bytes)
	[    0.004203] Inode-cache hash table entries: 32768 (order: 6, 262144 bytes)
	[    0.005056] Mount-cache hash table entries: 1024 (order: 1, 8192 bytes)
	[    0.005876] Mountpoint-cache hash table entries: 1024 (order: 1, 8192 bytes)
	[    0.007069] Last level iTLB entries: 4KB 64, 2MB 8, 4MB 8
	[    0.008003] Last level dTLB entries: 4KB 64, 2MB 0, 4MB 0, 1GB 4
	[    0.008756] Spectre V2 : Mitigation: Full generic retpoline
	[    0.009443] Spectre V2 : Spectre v2 mitigation: Filling RSB on context switch
	[    0.010320] Spectre V2 : Spectre v2 mitigation: Enabling Indirect Branch Prediction Barrier
	[    0.011342] Spectre V2 : Enabling Restricted Speculation for firmware calls
	[    0.012002] Speculative Store Bypass: Vulnerable
	[    0.023212] Freeing SMP alternatives memory: 28K
	[    0.024697] smpboot: Max logical packages: 1
	[    0.025231] Not enabling interrupt remapping due to skipped IO-APIC setup
	[    0.026091] smpboot: CPU0: Intel(R) Xeon(R) Processor @ 2.50GHz (family: 0x6, model: 0x55, stepping: 0x4)
	[    0.027322] Performance Events: unsupported p6 CPU model 85 no PMU driver, software events only.
	[    0.028000] Hierarchical SRCU implementation.
	[    0.028000] smp: Bringing up secondary CPUs ...
	[    0.028000] smp: Brought up 1 node, 1 CPU
	[    0.028004] smpboot: Total of 1 processors activated (5000.02 BogoMIPS)
	[    0.028899] devtmpfs: initialized
	[    0.029357] x86/mm: Memory block size: 128MB
	[    0.030060] clocksource: jiffies: mask: 0xffffffff max_cycles: 0xffffffff, max_idle_ns: 7645041785100000 ns
	[    0.031265] futex hash table entries: 256 (order: 2, 16384 bytes)
	[    0.032188] NET: Registered protocol family 16
	[    0.032869] cpuidle: using governor ladder
	[    0.033373] cpuidle: using governor menu
	[    0.037252] HugeTLB registered 1.00 GiB page size, pre-allocated 0 pages
	[    0.038082] HugeTLB registered 2.00 MiB page size, pre-allocated 0 pages
	[    0.039061] dmi: Firmware registration failed.
	[    0.039672] NetLabel: Initializing
	[    0.040005] NetLabel:  domain hash size = 128
	[    0.040540] NetLabel:  protocols = UNLABELED CIPSOv4 CALIPSO
	[    0.041249] NetLabel:  unlabeled traffic allowed by default
	[    0.042013] clocksource: Switched to clocksource kvm-clock
	[    0.042707] VFS: Disk quotas dquot_6.6.0
	[    0.043206] VFS: Dquot-cache hash table entries: 512 (order 0, 4096 bytes)
	[    0.043924] NET: Registered protocol family 2
	[    0.044640] TCP established hash table entries: 4096 (order: 3, 32768 bytes)
	[    0.045536] TCP bind hash table entries: 4096 (order: 4, 65536 bytes)
	[    0.046375] TCP: Hash tables configured (established 4096 bind 4096)
	[    0.047208] UDP hash table entries: 256 (order: 1, 8192 bytes)
	[    0.047924] UDP-Lite hash table entries: 256 (order: 1, 8192 bytes)
	[    0.048720] NET: Registered protocol family 1
	[    0.049931] virtio-mmio: Registering device virtio-mmio.0 at 0xd0000000-0xd0000fff, IRQ 5.
	[    0.050967] platform rtc_cmos: registered platform RTC device (no PNP device found)
	[    0.052017] Scanning for low memory corruption every 60 seconds
	[    0.052906] audit: initializing netlink subsys (disabled)
	[    0.053785] Initialise system trusted keyrings
	[    0.054339] Key type blacklist registered
	[    0.054867] audit: type=2000 audit(1559161506.409:1): state=initialized audit_enabled=0 res=1
	[    0.055940] workingset: timestamp_bits=36 max_order=17 bucket_order=0
	[    0.057690] squashfs: version 4.0 (2009/01/31) Phillip Lougher
	[    0.060260] Key type asymmetric registered
	[    0.060772] Asymmetric key parser 'x509' registered
	[    0.061390] Block layer SCSI generic (bsg) driver version 0.4 loaded (major 254)
	[    0.062325] io scheduler noop registered (default)
	[    0.062943] io scheduler cfq registered
	[    0.063466] virtio-mmio virtio-mmio.0: Failed to enable 64-bit or 32-bit DMA.  Trying to continue, but this might not work.
	[    0.064995] Serial: 8250/16550 driver, 1 ports, IRQ sharing disabled
	[    0.087340] serial8250: ttyS0 at I/O 0x3f8 (irq = 4, base_baud = 115200) is a U6_16550A
	[    0.089627] loop: module loaded
	[    0.090486] tun: Universal TUN/TAP device driver, 1.6
	[    0.091158] hidraw: raw HID events driver (C) Jiri Kosina
	[    0.091864] nf_conntrack version 0.5.0 (4096 buckets, 16384 max)
	[    0.092707] ip_tables: (C) 2000-2006 Netfilter Core Team
	[    0.093403] Initializing XFRM netlink socket
	[    0.093991] NET: Registered protocol family 10
	[    0.094874] Segment Routing with IPv6
	[    0.095347] NET: Registered protocol family 17
	[    0.095904] Bridge firewalling registered
	[    0.096450] sched_clock: Marking stable (96409925, 0)->(180526392, -84116467)
	[    0.097460] registered taskstats version 1
	[    0.097972] Loading compiled-in X.509 certificates
	[    0.099192] Loaded X.509 cert 'Build time autogenerated kernel key: 3472798b31ba23b86c1c5c7236c9c91723ae5ee9'
	[    0.100489] zswap: default zpool zbud not available
	[    0.101105] zswap: pool creation failed
	[    0.101681] Key type encrypted registered
	[    0.102938] EXT4-fs (vda): mounted filesystem with ordered data mode. Opts: (null)
	[    0.103870] VFS: Mounted root (ext4 filesystem) readonly on device 254:0.
	[    0.105005] devtmpfs: mounted
	[    0.106129] Freeing unused kernel memory: 1268K
	[    0.112030] Write protecting the kernel read-only data: 12288k
	[    0.113898] Freeing unused kernel memory: 2016K
	[    0.115661] Freeing unused kernel memory: 584K
	OpenRC init version 0.35.5.87b1ff59c1 starting
	Starting sysinit runlevel

	   OpenRC 0.35.5.87b1ff59c1 is starting up Linux 4.14.55-84.37.amzn2.x86_64 (x86_64)

	 * Mounting /proc ...
	 [ ok ]
	 * Mounting /run ...
	 * /run/openrc: creating directory
	 * /run/lock: creating directory
	 * /run/lock: correcting owner
	 * Caching service dependencies ...
	Service `hwdrivers' needs non existent service `dev'
	 [ ok ]
	Starting boot runlevel
	 * Remounting devtmpfs on /dev ...
	 [ ok ]
	 * Mounting /dev/mqueue ...
	 [ ok ]
	 * Mounting /dev/pts ...
	 [ ok ]
	 * Mounting /dev/shm ...
	 [ ok ]
	 * Setting hostname ...
	 [ ok ]
	 * Checking local filesystems  ...
	 [ ok ]
	 * Remounting root filesystem read/write ...
	[    0.239740] EXT4-fs (vda): re-mounted. Opts: data=ordered
	 [ ok ]
	 * Remounting filesystems ...[    0.241974] random: fast init done

	 [ ok ]
	 * Mounting local filesystems ...
	 [ ok ]
	 * Loading modules ...
	modprobe: can't change directory to '/lib/modules': No such file or directory
	modprobe: can't change directory to '/lib/modules': No such file or directory
	 [ ok ]
	 * Mounting misc binary format filesystem ...
	 [ ok ]
	 * Mounting /sys ...
	 [ ok ]
	 * Mounting security filesystem ...
	 [ ok ]
	 * Mounting debug filesystem ...
	 [ ok ]
	 * Mounting SELinux filesystem ...
	 [ ok ]
	 * Mounting persistent storage (pstore) filesystem ...
	 [ ok ]
	nomodules is an invalid runlevel
	Starting default runlevel
	[    1.056034] clocksource: tsc: mask: 0xffffffffffffffff max_cycles: 0x24094323722, max_idle_ns: 440795281912 ns

	Welcome to Alpine Linux 3.8
	Kernel 4.14.55-84.37.amzn2.x86_64 on an x86_64 (ttyS0)

	localhost login: 2019-05-29T20:26:06.302467920 [anonymous-instance:ERROR:vmm/src/lib.rs:1482] Failed to log metrics: Logger was not initialized.
	root
	Password: 
	Welcome to Alpine!

	The Alpine Wiki contains a large amount of how-to guides and general
	information about administrating Alpine systems.
	See <http://wiki.alpinelinux.org>.

	You can setup the system with the command: setup-alpine

	You may change this message by editing /etc/motd.

	login[858]: root login on 'ttyS0'
	localhost:~#
	```

	Use `root` as login and password.

