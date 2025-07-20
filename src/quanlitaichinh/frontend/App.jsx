// Redux Selector / Action
import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { setSetting } from "./store/setting/action";

function App({ children }) {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(setSetting());
  }, [dispatch]); // chỉ chạy 1 lần khi App render lần đầu

  return (
    <div className="app">
      {children}
    </div>
  );
}

export default App;
