import User from "../../type/User.tsx";

export default interface AuthContextType {
    user: User;
    login: () => void;
    logout: () => void;
}
