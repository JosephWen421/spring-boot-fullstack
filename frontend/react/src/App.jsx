import {Card, Spinner, Text, Wrap, WrapItem} from "@chakra-ui/react";
import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card.jsx";

const App = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    setTimeout(() => {
      getCustomers().then(res => {
        setCustomers(res.data)
        console.log(res.data)
      }).catch(err => {
        console.log(err)
      }).finally(() => {
        setLoading(false)
      })
    }, 1000)
  }, []);

  if (loading) {
    return (
        <SidebarWithHeader>
          <Spinner
              thickness='4px'
              speed='0.65s'
              emptyColor='gray.200'
              color='blue.500'
              size='xl'
          />
        </SidebarWithHeader>
    )
  }

  if (customers.length <= 0) {
    return (
        <SidebarWithHeader>
          <Text>No customer available</Text>
        </SidebarWithHeader>
    )
  }

  return (
      <SidebarWithHeader>
        <Wrap justify={"center"} spacing={"30px"}>
          {customers.map((customer, index) => (
              <WrapItem key={index}>
                <CardWithImage
                    {...customer}
                />
              </WrapItem>
          ))}
        </Wrap>
      </SidebarWithHeader>
  )
}

export default App