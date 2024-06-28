package main

import (
	"fmt"
	"runtime"
)

func main() {
	// Allocate 500MB of memory
	mem := make([]byte, 500*1024*1024)

	// Use the memory to prevent it from being optimized away
	for i := range mem {
		mem[i] = byte(i)
	}

	fmt.Println("Allocated 500MB of memory")

	// Use 1 CPU core
	runtime.GOMAXPROCS(1)

	// Infinite loop to keep the CPU busy
	for {
	}
}
