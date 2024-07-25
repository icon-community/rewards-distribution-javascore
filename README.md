# rewards distribution javascore

This repository contains the code for the rewards distribution smart contract (source code) and the scripts for deploying the contract, funding the contract and adding rewards to users.

The smart contract allows for the distribution of rewards to a list of addresses.

A list of admins can be defined and used to add the users and their rewards. By default the contract owner is an admin and only the contract owner can add other admins.

## Contract methods.

### getBALNContract
Returns the list of admins.

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_call",
  "params": {
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "getBALNContract"
    }
  }
}
```

Response:
```json
{
  "jsonrpc": "2.0",
  "result": "cx0169...063",
  "id": 1234
}
```

### isAdmin
Returns true if the address is an admin, false otherwise.

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_call",
  "params": {
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "isAdmin",
      "params": {
        "account": "WALLET_ADDRESS"
      }
    }
  }
}
```

Response:
```json
{
  "jsonrpc": "2.0",
  "result": "true",
  "id": 1234
}
```

### getICXClaimableAmount
Returns the claimable amount for the given address. The value returned is the amount of ICX in loop units as a hex string (1 ICX = 10^18 loop).

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_call",
  "params": {
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "getICXClaimableAmount",
      "params": {
        "user": "WALLET_ADDRESS"
      }
    }
  }
}
```

Response:
```json
{
  "jsonrpc": "2.0",
  "result": "0x123a00",
  "id": 1234
}
```

### getBALNClaimableAmount
Returns the claimable amount of BALN for the given address. The value returned is the amount of BALN in loop units as a hex string (1 BALN = 10^18 loop).

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_call",
  "params": {
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "getBALNClaimableAmount",
      "params": {
        "user": "WALLET_ADDRESS"
      }
    }
  }
}
```

Response:
```json
{
  "jsonrpc": "2.0",
  "result": "0x123a00",
  "id": 1234
}
```

### setBALNContract
Sets the address of the BALN token contract. Only the contract owner can set the address of the BALN token contract.

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_sendTransaction",
  "params": {
    "from": "WALLET_ADDRESS_SENDER",
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "setBALNContract",
      "params": {
        "balnContractAddress": "CONTRACT_ADDRESS"
      }
    }
    ...
  }
}
```

Response:

If the transaction is a success an event of type `ContractAdded(Address)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0x4007cfc8b56aef59fe09d18eb02484a046048c694ce43a5a4de32b90cda88b9e

### addAdmin
Adds an admin to the list of admins. Only the contract owner can add admins.

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_sendTransaction",
  "params": {
    "from": "WALLET_ADDRESS_SENDER",
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "addAdmin",
      "params": {
        "admin": "WALLET_ADDRESS_ADMIN"
      }
    }
    ...
  }
}
```

Response:

If the transaction is a success an event of type `AdminAdded(Address)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0x4a3820fe60d996de09f31a34ee67b86fd6215dc2c869545986c9ecd8bf7abff3#events

### removeAdmin
Removes an admin from the list of admins. Only the contract owner can remove admins.

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_sendTransaction",
  "params": {
    "from": "WALLET_ADDRESS_SENDER",
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "removeAdmin",
      "params": {
        "admin": "WALLET_ADDRESS_ADMIN"
      }
    }
    ...
  }
}
```

Response:

If the transaction is a success an event of type `AdminRemoved(Address)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0xccfa4f7cc0f8703e2eb65d34880c848200c5aa36c6a7152f4c450f471c96920f#events

### addICXClaim
Adds an ICX claim to the user. Only admins can add claims. The amount param is in loop units as a hex string (1 ICX = 10^18 loop) and the user param is the address of the user to add the claim.

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_sendTransaction",
  "params": {
    "from": "WALLET_ADDRESS_SENDER",
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "addICXClaim",
      "params": {
        "user": "WALLET_ADDRESS",
        "amount": "0x1223aa00"
      }
    }
    ...
  }
}
```

Response:

If the transaction is a success an event of type `ClaimAdded(Address,int,str)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0xb46c91d011aede22bbd2cf0c669c3eb0f157916c9854a82b077026c7881e6f6f#events

### addBALNClaim
Adds an BALN claim to the user. Only admins can add claims. The amount param is in loop units as a hex string (1 BALN = 10^18 loop) and the user param is the address of the user to add the claim.

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_sendTransaction",
  "params": {
    "from": "WALLET_ADDRESS_SENDER",
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "addBALNClaim",
      "params": {
        "user": "WALLET_ADDRESS",
        "amount": "0x1223aa00"
      }
    }
    ...
  }
}
```

Response:

If the transaction is a success an event of type `ClaimAdded(Address,int,str)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0x5c7aa31b8c75a7bd478c6cc757e8900327e35ac93e335081806927baa701b044#events

### claimICX
Claims the ICX rewards for the user. The amount is transferred to the user address. This transaction must be called by the user that wants to claim the rewards.

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_sendTransaction",
  "params": {
    "from": "WALLET_ADDRESS_SENDER",
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "claimICX"
    }
    ...
  }
}
```

Response:

If the transaction is a success an event of type `Claimed(Address,int,str)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0x803366fd82e472c9559e88fbd4e361dba74c6c50e13ee362fd6ea111e2160413#events

### claimBALN
Claims the BALN rewards for the user. The amount is transferred to the user address. This transaction must be called by the user that wants to claim the rewards.

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_sendTransaction",
  "params": {
    "from": "WALLET_ADDRESS_SENDER",
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "claimBALN"
    }
    ...
  }
}
```

Response:

If the transaction is a success an event of type `Claimed(Address,int,str)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0x943fcaf10149ccb7cc0cc602494f079c90112c7ddedf9e9096bfb3054bc20de6

### adminClaimICX
This function is used to recover the ICX balance of the contract. Any admin can call this function and the balance will be transferred to the contract owner address. The amount is in loop units as a hex string (1 ICX = 10^18 loop).

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_sendTransaction",
  "params": {
    "from": "WALLET_ADDRESS_SENDER",
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "adminClaimICX",
      "params": {
        "amount": "0x1223aa00"
      }
    }
    ...
  }
}
```

Response:

If the transaction is a success an event of type `OwnerClaimed(Address,int,str)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0x9012cb76f918613d37a0cd1b8ca9178a30072f2aec9f6399dc8f90e1835b5eab#events

### adminClaimBALN
This function is used to recover the BALN balance of the contract. Any admin can call this function and the balance will be transferred to the contract owner address. The amount is in loop units as a hex string (1 BALN = 10^18 loop).

Request:
```json
{
  "jsonrpc": "2.0",
  "id": 1234,
  "method": "icx_sendTransaction",
  "params": {
    "from": "WALLET_ADDRESS_SENDER",
    "to": "CONTRACT_ADDRESS",
    "dataType": "call",
    "data": {
      "method": "adminClaimBALN",
      "params": {
        "amount": "0x1223aa00"
      }
    }
    ...
  }
}
```

Response:

If the transaction is a success an event of type `OwnerClaimed(Address,int,str)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0xb9db1186bcbebfe78c1a8cc7f031ec69f826ef27cdfe611d4d14b1d3ad81d42d#events

## Contract management scripts.
To execute the management scripts first clone the project and install the dependencies.

```bash
git clone GIT_URL
cd rewards-distribution-javascore
npm install
```

### Set the environment variables
Create a `.env` file in the root of the project with the following content:

```bash
PRIVATE_KEY="YOUR_PRIVATE_KEY"
NETWORK="mainnet" # or "lisbon"
CONTRACT_ADDRESS="CONTRACT_ADDRESS" # defined after deploying the contract
```
A wallet with enough funds is required to deploy the contract. And once the contract is deployed you can fund the contract by sending ICX to the contract address.

### Deploy contract
This script deploys the contract to the ICON network. The contract owner is the wallet address that deploys the contract.

```bash
npm run deploy
```
If there are errors running the deploy script, check that a valid private key wallet with enough funds is defined in the `.env` file as well as the correct network (200 ICX balance on the wallet should be enough to trigger the transaction, the cost of the deployment is probably around 10-30 ICX, it varies).

You can also locally compile the contracts by running the following command inside the `./smart-contract` folder.

```bash
./gradlew clean build optimizedJar
```

A Makefile is also provided to compile the contracts.

You need to have gradle installed in your system to compile the contracts.


### Update contract.

To update the contract you need to compile the contract and then run the update script.

```bash
npm run update-contract
```

### Setup contract

This script is used to setup the contract after deploying it. It setup the address of the BALN token contract for correctly handling the BALN rewards.

```bash
npm run setup-contract
```

### Fund contract
You can fund the contract directly with your wallet by sending ICX or BALN to the contract address, or you can use the following scripts to fund the contract.

```bash
npm run icx-fund-contract
npm run baln-fund-contract
```

Before running the command you need to update the `FUND_AMOUNT` variable in the `icx-fund-contract.js` and `baln-fund-contract.js` files located in the `./scripts` folder at the root of the project, and add the contract address in the `CONTRACT_ADDRESS` variable of the `.env` file.

```javascript
// inside the icx-fund-contract.js file
const FUND_AMOUNT = "100"; // 100 ICX
```

```bash
# inside the .env file
CONTRACT_ADDRESS="CONTRACT_ADDRESS"
```

### add user rewards
The following commands can be used to batch send transactions that will add ICX or BALN rewards to different users.

```bash
npm run icx-add-rewards
npm run baln-add-rewards
```

Before running the script you need to update the `users.json` file  located in the `./data` folder at the root of the project, with the list of users and the amount of rewards to add.

The amounts are in ICX units if funding ICX rewards and in BALN units if funding BALN rewards.

```json
[
  {
    "address": "hx0169...063",
    "amount": "10"
  },
  {
    "address": "hxf859...c62",
    "amount": "20"
  }
]
```

### add admins and recover contract balance
The easiest way to add admins and recover the contract balance is to simply sign these transactions using the tracker UI for the contract.

Login with your admin (or contract owner wallet) in the tracker, search for the contract address and then in the `Contract` tab you can call the `addAdmin` and `adminClaim` functions.
