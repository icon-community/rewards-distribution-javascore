# Rewards Distribution Smart Contract

A comprehensive smart contract for distributing multiple token types (ICX, BALN, BNUSD) to users on the ICON blockchain. This repository contains the smart contract source code and management scripts for deploying, funding, and distributing rewards.

## 🚀 Features

- **Multi-Token Support**: Distribute ICX, BALN, and BNUSD tokens
- **Admin Management**: Secure admin system with role-based access control
- **Batch Operations**: Efficiently add rewards to multiple users
- **Automatic Claiming**: Users can claim their rewards directly
- **Contract Recovery**: Admins can recover contract balances
- **Event Logging**: Comprehensive event tracking for all operations

## 📋 Table of Contents

- [Smart Contract Methods](#smart-contract-methods)
- [Quick Start](#quick-start)
- [Installation & Setup](#installation--setup)
- [Contract Management](#contract-management)
- [Scripts Reference](#scripts-reference)
- [Configuration](#configuration)
- [Testing](#testing)

## 🏗️ Smart Contract Methods

### Query Methods (Read-Only)

#### `getBALNContract()`
Returns the BALN token contract address.

#### `getBNUSDContract()`
Returns the BNUSD token contract address.

#### `isAdmin(Address account)`
Returns `true` if the address is an admin, `false` otherwise.

#### `getICXClaimableAmount(Address user)`
Returns the claimable ICX amount for the given user (in loop units).

#### `getBALNClaimableAmount(Address user)`
Returns the claimable BALN amount for the given user (in loop units).

#### `getBNUSDClaimableAmount(Address user)`
Returns the claimable BNUSD amount for the given user (in loop units).

### Transaction Methods

#### `setBALNContract(Address balnContractAddress)`
Sets the BALN token contract address. **Admin only.**

#### `setBNUSDContract(Address bnusdContractAddress)`
Sets the BNUSD token contract address. **Admin only.**

#### `addAdmin(Address admin)`
Adds an admin to the contract. **Contract owner only.**

#### `removeAdmin(Address admin)`
Removes an admin from the contract. **Contract owner only.**

#### `addICXClaim(Address user, BigInteger amount)`
Adds an ICX claim for a user. **Admin only.**

#### `addBALNClaim(Address user, BigInteger amount)`
Adds a BALN claim for a user. **Admin only.**

#### `addBNUSDClaim(Address user, BigInteger amount)`
Adds a BNUSD claim for a user. **Admin only.**

#### `claimICX()`
Claims ICX rewards for the caller.

#### `claimBALN()`
Claims BALN rewards for the caller.

#### `claimBNUSD()`
Claims BNUSD rewards for the caller.

#### `adminClaimICX(BigInteger amount)`
Recovers ICX from contract to owner. **Admin only.**

#### `adminClaimBALN(BigInteger amount)`
Recovers BALN from contract to owner. **Admin only.**

#### `adminClaimBNUSD(BigInteger amount)`
Recovers BNUSD from contract to owner. **Admin only.**

## ⚡ Quick Start

```bash
# 1. Clone and install
git clone <repository-url>
cd rewards-distribution-javascore
npm install

# 2. Set up environment
cp .env.example .env
# Edit .env with your private key and network

# 3. Deploy contract
npm run deploy

# 4. Update .env with deployed contract address

# 5. Setup contracts (sets token addresses)
npm run setup-contract

# 6. Fund the contract
npm run icx-fund-contract
npm run baln-fund-contract
npm run bnusd-fund-contract

# 7. Add rewards to users
npm run add-icx-rewards
npm run add-baln-rewards
npm run add-bnusd-rewards
```

## 🔧 Installation & Setup

### Prerequisites

- Node.js (v14 or higher)
- Java 11 or higher
- Gradle
- ICON wallet with sufficient balance

### Environment Configuration

Create a `.env` file in the project root:

```bash
# Required: Your wallet private key
PRIVATE_KEY="your_private_key_here"

# Required: Network to deploy to
NETWORK="mainnet"  # or "lisbon"

# Required: Contract address (set after deployment)
CONTRACT_ADDRESS=""
```

### Contract Compilation

```bash
# Navigate to smart contract directory
cd smart-contract

# Compile using Gradle
./gradlew clean build optimizedJar

# Or use the provided Makefile
make clean-build
```

## 🎯 Contract Management

### Deployment

```bash
npm run deploy
```

**Requirements:**
- Valid private key in `.env`
- Sufficient ICX balance (recommended: 200+ ICX)
- Correct network configuration

### Contract Setup

```bash
npm run setup-contract
```

This script:
- Sets the BALN token contract address
- Sets the BNUSD token contract address
- Validates the setup

### Funding the Contract

#### Fund with ICX
```bash
npm run icx-fund-contract
```

#### Fund with BALN
```bash
npm run baln-fund-contract
```

#### Fund with BNUSD
```bash
npm run bnusd-fund-contract
```

**Note:** Update the `FUND_AMOUNT` variable in the respective script files before running.

### Adding Rewards

#### Prepare User Data

Edit `data/users.json`:

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

#### Add Rewards

```bash
# Add ICX rewards
npm run add-icx-rewards

# Add BALN rewards  
npm run add-baln-rewards

# Add BNUSD rewards
npm run add-bnusd-rewards
```

## 📜 Scripts Reference

| Script | Description | Usage |
|--------|-------------|-------|
| `deploy` | Deploy the smart contract | `npm run deploy` |
| `update-contract` | Update existing contract | `npm run update-contract` |
| `setup-contract` | Configure token contracts | `npm run setup-contract` |
| `icx-fund-contract` | Fund contract with ICX | `npm run icx-fund-contract` |
| `baln-fund-contract` | Fund contract with BALN | `npm run baln-fund-contract` |
| `bnusd-fund-contract` | Fund contract with BNUSD | `npm run bnusd-fund-contract` |
| `add-icx-rewards` | Add ICX rewards to users | `npm run add-icx-rewards` |
| `add-baln-rewards` | Add BALN rewards to users | `npm run add-baln-rewards` |
| `add-bnusd-rewards` | Add BNUSD rewards to users | `npm run add-bnusd-rewards` |

## ⚙️ Configuration

### Token Addresses

Token addresses are configured in `utils/config.js`:

```javascript
token: {
  BALN: {
    mainnet: "cxf61cd5a45dc9f91c15aa65831a30a90d59a09619",
    lisbon: "cxc3c552054ba6823107b56086134c2afc26ab1dfa"
  },
  BNUSD: {
    mainnet: "cxf61cd5a45dc9f91c15aa65831a30a90d59a09619", 
    lisbon: "cx87f7f8ceaa054d46ba7343a2ecd21208e12913c6"
  }
}
```

### Network Configuration

```javascript
endpoint: {
  mainnet: {
    url: "ctz.solidwallet.io",
    nid: 1
  },
  lisbon: {
    url: "lisbon.net.solidwallet.io", 
    nid: 2
  }
}
```

## 🧪 Testing

### Run Tests

```bash
cd smart-contract
./gradlew clean test
```

### Test Coverage

The test suite covers:
- ✅ ICX token functionality
- ✅ BALN token functionality  
- ✅ BNUSD token functionality
- ✅ Admin management
- ✅ Access control
- ✅ Error handling
- ✅ Edge cases

## 🔐 Security Features

- **Role-based Access Control**: Only admins can add claims
- **Owner-only Admin Management**: Only contract owner can add/remove admins
- **Input Validation**: All inputs are validated
- **Event Logging**: All operations emit events for transparency
- **Safe Math**: Uses BigInteger for precise calculations

## 📊 Events

The contract emits the following events:

- `AdminAdded(Address admin)` - When an admin is added
- `AdminRemoved(Address admin)` - When an admin is removed
- `ContractAdded(Address contract)` - When a token contract is set
- `ClaimAdded(Address user, BigInteger amount, String token)` - When a claim is added
- `Claimed(Address user, BigInteger amount, String token)` - When a user claims rewards
- `OwnerClaimed(Address owner, BigInteger amount, String token)` - When admin recovers funds

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the ISC License.

## 🆘 Support

For issues and questions:
- Check the [Issues](../../issues) page
- Review the test files for usage examples
- Consult the ICON documentation

---

**Note:** Always test on the Lisbon testnet before deploying to mainnet. Ensure you have sufficient balance for deployment and operations.