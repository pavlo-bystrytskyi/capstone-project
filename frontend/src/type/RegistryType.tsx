type RegistryType = {
    code: RegistryTypeCode,
    description: string,
    restricted: boolean
}

export enum RegistryTypeCode {
    GUEST = "guest",
    CUSTOMER = "customer"
}

export const registryTypes: RegistryType[] = [
    {
        code: RegistryTypeCode.GUEST,
        description: "create_as_guest",
        restricted: false
    },
    {
        code: RegistryTypeCode.CUSTOMER,
        description: "create_as_customer",
        restricted: true
    }
]

export default RegistryType;