import httpx
import asyncio
import random
import time

BASE_URL = "https://10.50.16.16:8444/api/employees"
PATHS = [
    "/sortedByName",
    "/sortedById",
    "/sortedBySalary",
    "/sortedByDesignation",
    "/orderByName",
    "/orderBySalary"
]

TOTAL_REQUESTS = 200
MAX_CONNECTIONS = 4

async def fetch(client, url):
    response = await client.get(url)
    return response

async def main():
    urls = [BASE_URL + path for path in PATHS * (TOTAL_REQUESTS // len(PATHS))]
    start_time = time.time() * 1000

    async with httpx.AsyncClient(http2=True, verify=False, limits=httpx.Limits(max_connections=MAX_CONNECTIONS)) as client:
        tasks = [fetch(client, url) for url in urls]

        for task in asyncio.as_completed(tasks):
            try:
                response = await task
                # print(f"Response {response.text[:200]}")  # Print first 200 characters
                print(f"HTTP Version: {response.http_version} & HTTP URL: {response.url}")
            except Exception as e:
                print(f"Request failed: {e}")

        # responses = await asyncio.gather(*tasks, return_exceptions=True)
    
    # for i, response in enumerate(responses):
    #     if isinstance(response, Exception):
    #         print(f"Request {i} failed: {response}")
    #     else:
    #         print(f"Response {i} from {urls[i]}:\n{response.text[:200]}\n")  # Print first 200 characters
    #         print(f"HTTP Version: {response.http_version}")

    end_time = time.time() * 1000 
    delta = end_time - start_time

    print(f"Start time: {start_time:.2f} ms")
    print(f"End time: {end_time:.2f} ms")
    print(f"Delta time: {delta:.2f} ms")    

# # Run the main function
# asyncio.run(main())

async def parallel_main():
    # Create multiple instances of the main coroutine
    await asyncio.gather(*[main() for _ in range(MAX_CONNECTIONS)])

# Run the parallel_main function
asyncio.run(parallel_main())