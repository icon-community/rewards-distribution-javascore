# rewards distribution javascore

This repository contains the code for the rewards distribution smart contract (source code) and the scripts for deploying the contract, funding the contract and adding rewards to users.

The smart contract allows for the distribution of rewards to a list of addresses.

A list of admins can be defined and used to add the users and their rewards. By default the contract owner is an admin and only the contract owner can add other admins.

## Contract methods.

### getAdmins
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
      "method": "getAdmins"
    }
  }
}
```

Response:
```json
{
  "jsonrpc": "2.0",
  "result": [
    "hx0169...063",
    "hxf859...c62"
  ],
  "id": 1234
}
```

### getClaimableAmount
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
      "method": "getClaimableAmount",
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


### addClaim
Adds a claim to the user. Only admins can add claims. The amount param is in loop units as a hex string (1 ICX = 10^18 loop) and the user param is the address of the user to add the claim.

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
      "method": "addClaim",
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

If the transaction is a success an event of type `ClaimAdded(Address,int)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0x57c64432be514726b805279c0afe41f0caa0c5af1f7909aed1343f2ec3524a7e#events


### claim
Claims the rewards for the user. The amount is transferred to the user address. This transaction must be called by the user that wants to claim the rewards.

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
      "method": "claim"
    }
    ...
  }
}
```

Response:

If the transaction is a success an event of type `Claimed(Address,int)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0x4c110009c257c428653355cf39b596097b868e7c9c75803ee156e9162e366458#events

### adminClaim
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
      "method": "adminClaim",
      "params": {
        "amount": "0x1223aa00"
      }
    }
    ...
  }
}
```

Response:

If the transaction is a success an event of type `OwnerClaimed(Address,int)` is emitted, if not the transaction will fail.

Example transaction in the tracker:
https://tracker.lisbon.icon.community/transaction/0x0ec2877510196033ed5f2c01b79e2b059da9ead390fa3ab27397baa01b9e6587#events

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
If there are errors running the deploy script, check that a valid private key wallet with enough funds is defined in the `.env` file as well as the correct network.

You can also locally compile the contracts by running the following command inside the `./smart-contract` folder.

```bash
./gradlew clean build optimizedJar
```

A Makefile is also provided to compile the contracts.

You need to have gradle installed in your system to compile the contracts.

### Fund contract
You can fund the contract directly with your wallet by sending ICX to the contract address, or you can use the following script to fund the contract.

```bash
npm run fund-contract
```

Before running the command you need to update the `FUND_AMOUNT` variable in the `fund-contract.js` file located in the `./scripts` folder at the root of the project, and add the contract address in the `CONTRACT_ADDRESS` variable of the `.env` file.

```javascript
// inside the fund-contract.js file
const FUND_AMOUNT = "100"; // 100 ICX
```

```bash
# inside the .env file
CONTRACT_ADDRESS="CONTRACT_ADDRESS"
```

### add user rewards
The following command can be used to batch send transactions that will add rewards to different users.

```bash
npm run add-rewards
```

Before running the script you need to update the `users.json` file  located in the `./data` folder at the root of the project, with the list of users and the amount of rewards to add.

The amounts are in ICX units.

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
