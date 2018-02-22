### Setup
```
Create a list to track all databases with the last one in the list being the last created database
```

## Storing data
##### Note that all requests will be forwarded and handled on a single queue to prevent unreasonable creation of databases
```
Parallel search across all databases for key of storing data
    if entry has been found, notify duplicate and RETURN 

Foreach database in the list of databases:
    if database has enough space to store the data
        then store the entry and set INSERTED flag, immediately RETURN

If not INSERTED
    then create a database and store the entry
```

## Retrieving data
```
Parallel search across all databases for key
    if entry has been found, RETURN the data of the entry
    otherwise RETURN not found
```